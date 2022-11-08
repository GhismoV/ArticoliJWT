package it.ghismo.corso1.articoli.tests.ControllerTests;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.ghismo.corso1.articoli.Application;
import it.ghismo.corso1.articoli.errors.ResultEnum;


@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SelectArtTest
{
	private MockMvc mockMvc;
	
	private String tokenJwt = "";
	private static final String authJwtUrl = "http://localhost:9100/auth";
	private static final String authJwtUid = "Ghismo";
	private static final String authJwtPwd = "banana";
	private static final ObjectMapper om = new ObjectMapper();
		
	@Autowired
	private WebApplicationContext wac;
	
	
	
	@BeforeEach
	public void setup() throws JSONException, IOException
	{
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(springSecurity()) // ghismo - attiva la sicurezza nei test
				.build();	
		
		if(this.tokenJwt.length() == 0) {
			getTokenJwt();
		}
		
	}
	
	private void getTokenJwt() {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		JSONObject rq = new JSONObject();
		try {
			rq.put("username", authJwtUid);
			rq.put("password", authJwtPwd);
		} catch (JSONException e) {}
		
		HttpEntity<String> httpE = new HttpEntity<String>(rq.toString(), headers);
		String rs = rt.postForObject(authJwtUrl, httpE, String.class);
		
		try {
			this.tokenJwt = "Bearer " + om.readTree(rs).path("token").asText("");
		} catch (JsonProcessingException e) {
			this.tokenJwt = "sta ceppa";
		}
		
		System.out.println("ghismo - token ricavato dal test:" + this.tokenJwt);
		
	}
	
	String JsonData =  
			"{\n" + 
			"    \"codArt\": \"002000301\",\n" + 
			"    \"descrizione\": \"ACQUA ULIVETO 15 LT\",\n" + 
			"    \"um\": \"PZ\",\n" + 
			"    \"codStat\": \"\",\n" + 
			"    \"pzCart\": 6,\n" + 
			"    \"pesoNetto\": 1.5,\n" + 
			"    \"idStatoArt\": \"1\",\n" + 
			"    \"dataCreazione\": \"2010-06-14\",\n" + 
			"    \"prezzo\": 1.07,\n" + 
			"    \"barcode\": [\n" + 
			"        {\n" + 
			"            \"barcode\": \"8008490000021\",\n" + 
			"            \"idTipoArt\": \"CP\"\n" + 
			"        }\n" + 
			"    ],\n" + 
			"    \"famAssort\": {\n" + 
			"        \"id\": 1,\n" + 
			"        \"descrizione\": \"DROGHERIA ALIMENTARE\"\n" + 
			"    },\n" + 
			"    \"ingredienti\": null,\n" + 
			"    \"iva\": {\n" + 
			"        \"idIva\": 22,\n" + 
			"        \"descrizione\": \"IVA RIVENDITA 22%\",\n" + 
			"        \"aliquota\": 22\n" + 
			"    }\n" + 
			"}";
	
	@Test
	@Order(1)
	public void listArtByEan() throws Exception
	{
		System.out.println("ghismo - token ricavato dal test 111:" + this.tokenJwt);
		
		mockMvc
		.perform(MockMvcRequestBuilders
				.get("/api/articoli/cerca/barcode/8008490000021")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			 //articoli
			.andExpect(jsonPath("$.codArt").exists())
			.andExpect(jsonPath("$.codArt").value("002000301"))
			.andExpect(jsonPath("$.descrizione").exists())
			.andExpect(jsonPath("$.descrizione").value("ACQUA ULIVETO 15 LT"))
			.andExpect(jsonPath("$.um").exists())
			.andExpect(jsonPath("$.um").value("PZ"))
			.andExpect(jsonPath("$.codStat").exists())
			.andExpect(jsonPath("$.codStat").value(""))
			.andExpect(jsonPath("$.pzCart").exists())
			.andExpect(jsonPath("$.pzCart").value("6"))
			.andExpect(jsonPath("$.pesoNetto").exists())
			.andExpect(jsonPath("$.pesoNetto").value("1.5"))
			.andExpect(jsonPath("$.idStatoArt").exists())
			.andExpect(jsonPath("$.idStatoArt").value("1"))
			.andExpect(jsonPath("$.dataCreazione").exists())
			.andExpect(jsonPath("$.dataCreazione").value("2010-06-14"))
			.andExpect(jsonPath("$.prezzo").value("1.07"))
			 //barcode
			.andExpect(jsonPath("$.barcode[0].barcode").exists())
			.andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021")) 
			.andExpect(jsonPath("$.barcode[0].idTipoArt").exists())
			.andExpect(jsonPath("$.barcode[0].idTipoArt").value("CP")) 
			 //famAssort
			.andExpect(jsonPath("$.famAssort.id").exists())
			.andExpect(jsonPath("$.famAssort.id").value("1")) 
			.andExpect(jsonPath("$.famAssort.descrizione").exists())
			.andExpect(jsonPath("$.famAssort.descrizione").value("DROGHERIA ALIMENTARE")) 
			 //ingredienti
			.andExpect(jsonPath("$.ingredienti").isEmpty())
			 //Iva
			.andExpect(jsonPath("$.iva.idIva").exists())
			.andExpect(jsonPath("$.iva.idIva").value("22")) 
			.andExpect(jsonPath("$.iva.descrizione").exists())
			.andExpect(jsonPath("$.iva.descrizione").value("IVA RIVENDITA 22%"))
			.andExpect(jsonPath("$.iva.aliquota").exists())
			.andExpect(jsonPath("$.iva.aliquota").value("22"))	
			
			.andDo(print());
	}
	
	private String Barcode = "8008490002138";
	
	@Test
	@Order(2)
	public void ErrlistArtByEan() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
					.get("/api/articoli/cerca/barcode/" + Barcode)
					.header("Authorization", this.tokenJwt)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(ResultEnum.NameKeySearchNotFound.getResult().code()))
				.andExpect(jsonPath("$.message").value("Barcode con chiave di ricerca [" + Barcode + "] non trovato"))
				.andDo(print());
	}
	
	@Test
	@Order(3)
	public void listArtByCodArt() throws Exception
	{
		mockMvc
		.perform(MockMvcRequestBuilders
				.get("/api/articoli/cerca/codice/002000301")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(content().json(JsonData)) 
		.andExpect(jsonPath("$.prezzo").value("1.07"))
		.andReturn();
	}
	
	private String CodArtBad = "002x";
	private String CodArtNotFound = "0020003019";
	
	@Test
	@Order(4)
	public void errlistArtByCodArt1() throws Exception
	{
		mockMvc
		.perform(MockMvcRequestBuilders
				.get("/api/articoli/cerca/codice/" + CodArtNotFound)
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("2"))
		.andExpect(jsonPath("$.message").value("L'Articolo con chiave di ricerca [" + CodArtNotFound + "] non trovato"))
		.andDo(print());
	}
	
	@Test
	@Order(5)
	public void errlistArtByCodArt2() throws Exception
	{
		mockMvc
		.perform(MockMvcRequestBuilders
				.get("/api/articoli/cerca/codice/" + CodArtBad)
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$[0].code").value("FORMAT_ERROR: cercaByArtCode.codArt"))
		.andExpect(jsonPath("$[0].message").value("javax.validation.constraints.Pattern.message"))
		.andDo(print());
	}	
	private String JsonData2 = "[" + JsonData + "]";

	@Test
	@Order(6)
	public void listArtByDesc() throws Exception
	{
		mockMvc
		.perform(MockMvcRequestBuilders
				.get("/api/articoli/cerca/descrizione/ACQUA ULIVETO 15 LT")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(content().json(JsonData2)) 
		.andExpect(jsonPath("$[0].prezzo").value("1.07"))
		.andReturn();
	}
}
