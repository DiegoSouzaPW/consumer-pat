package br.com.alelo.consumer.consumerpat.service;

import br.com.alelo.consumer.consumerpat.dto.BuyDto;
import br.com.alelo.consumer.consumerpat.dto.ExtractDto;

public interface IExtraticService {
	
    public ExtractDto saveExtract(BuyDto buyDto);

}
