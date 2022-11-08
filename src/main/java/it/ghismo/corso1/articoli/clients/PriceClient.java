package it.ghismo.corso1.articoli.clients;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import it.ghismo.corso1.articoli.security.UtentiDto;

@FeignClient(name = "PriceArtService", url = "localhost:5071")
public interface PriceClient {
	@GetMapping("/api/prezzi/cerca/{codArt}")
	Double getPriceArtDefListino(
			@RequestHeader("Authorization") String headerAuthStr, 
			@PathVariable("codArt") String codiceArticolo);

	@GetMapping("/api/prezzi/cerca/{codArt}/{idList}")
	Double getPriceArtListino(
			@RequestHeader("Authorization") String headerAuthStr, 
			@PathVariable("codArt") String codiceArticolo,
			@PathVariable("idList") String idListino);
	
	default Double getPriceArt(UtentiDto currentUser, String codiceArticolo, String idListino) {
		// calcola token basic
		String basicAuthUserCredential = currentUser.getUserId() + ":" + currentUser.getPassword();
		System.out.println("ghismo - credenziali magiche:" + basicAuthUserCredential);
		String basicToken = "Basic " +  new String(Base64.getEncoder().encode(basicAuthUserCredential.getBytes(Charset.forName("US-ASCII"))));
		return Objects.nonNull(idListino) 
				? getPriceArtListino(basicToken, codiceArticolo, idListino) 
				: getPriceArtDefListino(basicToken, codiceArticolo);
	}
	

}
