package br.com.alelo.consumer.consumerpat.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.alelo.consumer.consumerpat.dto.ConsumerDto;
import br.com.alelo.consumer.consumerpat.entity.Card;
import br.com.alelo.consumer.consumerpat.entity.Consumer;
import br.com.alelo.consumer.consumerpat.exception.CardIsExistException;
import br.com.alelo.consumer.consumerpat.exception.ConsumerNotFoundException;
import br.com.alelo.consumer.consumerpat.exception.DocumentIsExistException;
import br.com.alelo.consumer.consumerpat.respository.CardRepository;
import br.com.alelo.consumer.consumerpat.respository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.respository.ExtractRepository;
import br.com.alelo.consumer.consumerpat.service.IConsumerService;
import br.com.alelo.consumer.consumerpat.utils.CopyProperties;
import br.com.alelo.consumer.consumerpat.utils.UUIDUtils;

@Service
public class ConsumerService implements IConsumerService {

	@Autowired
	ConsumerRepository consumerRepository;

	@Autowired
	ExtractRepository extractRepository;
	
	@Autowired
	CardRepository cardRepository;
	
	@Cacheable("getConsumer")
	public Consumer getConsumer(String id) {
		return  consumerRepository.findById(UUIDUtils.makeUuid(id)).orElseThrow(() -> new ConsumerNotFoundException("Consumer not found!"));
	}

	@Override
	@Cacheable("getAllConsumers")
	public Page<Consumer> getAllConsumers(Pageable pageable) {
		return consumerRepository.findAll(pageable);
	}

	@Override
	@CacheEvict(value={"getAllConsumers", "getConsumer"}, allEntries=true)
	public Consumer createConsumer(ConsumerDto consumerDto) throws Exception {
		
		Consumer consumer = new Consumer();
		consumer = CopyProperties.consumerDtoToConsumer(consumerDto, false);
		
		Optional<Optional<Card>> cardOptional = consumer.getCards().stream().map(card -> {
			return cardRepository.findByCardNumberAndTypeCard(card.getCardNumber(), card.getTypeCard());
		}).findAny();

		if (cardOptional.get().isPresent()) {
			throw new CardIsExistException("Card "+ cardOptional.get().get().getTypeCard().getTypeCard() + " " + cardOptional.get().get().getCardNumber() + " is exist in database");
		} 
		
		Optional<Consumer> documentOptional = consumerRepository.findByDocumentNumber(consumer.getDocumentNumber());
		if (documentOptional.isPresent()) {
			throw new DocumentIsExistException("Document "+ documentOptional.get().getDocumentNumber() + "is exist in database");
		} 
		
		return consumerRepository.save(consumer);

	}

	@Override
	@CacheEvict(value={"getAllConsumers", "getConsumer"}, allEntries=true)
	public Consumer updateConsumer(ConsumerDto consumerDto) {
		
		Optional<Consumer> consumerOptional = consumerRepository.findByDocumentNumber(consumerDto.getDocumentNumber());
		if (!consumerOptional.isPresent()) {
			throw new ConsumerNotFoundException("Consumer not found!");
		} 
		
		Consumer consumer = consumerOptional.get();
		consumer = CopyProperties.consumerDtoToConsumer(consumerDto, true);
		consumer.setIdConsumer(consumerOptional.get().getIdConsumer());
	
		return consumerRepository.save(consumer);

	}

	@Override
	@CacheEvict(value={"getAllConsumers", "getConsumer"}, allEntries=true)
	public ResponseEntity<String> deleteConsumer(String id) {
		
		Optional<Consumer> consumerOptional = consumerRepository.findById(UUIDUtils.makeUuid(id));
		if (!consumerOptional.isPresent()) {
			throw new ConsumerNotFoundException("Consumer not found!");
		}
		consumerRepository.delete(consumerOptional.get());
		
		return new ResponseEntity<>(id, HttpStatus.OK);

	}

}
