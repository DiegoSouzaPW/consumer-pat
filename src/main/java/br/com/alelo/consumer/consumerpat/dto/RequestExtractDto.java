package br.com.alelo.consumer.consumerpat.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestExtractDto implements Serializable {

	private static final long serialVersionUID = -4617709413953892572L;

    private String idConsumer;
        
}

