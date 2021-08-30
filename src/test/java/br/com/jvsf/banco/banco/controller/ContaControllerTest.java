package br.com.jvsf.banco.banco.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jvsf.banco.banco.BancoApplication;
import br.com.jvsf.banco.banco.PayloadExtractor;
import br.com.jvsf.banco.banco.config.ResponseExceptionHandler.Erro;
import br.com.jvsf.banco.banco.dto.ContaDTO;
import br.com.jvsf.banco.banco.dto.LancamentoDTO;
import br.com.jvsf.banco.banco.dto.SaldoDTO;
import br.com.jvsf.banco.banco.form.ContaForm;
import br.com.jvsf.banco.banco.form.DepositoForm;
import br.com.jvsf.banco.banco.form.LancamentoForm;
import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;
import br.com.jvsf.banco.banco.repository.ContaRepository;
import br.com.jvsf.banco.banco.repository.LancamentoRepository;
import br.com.jvsf.banco.banco.repository.filter.ContaFilter;
import br.com.jvsf.banco.banco.service.ContaService;


@Sql(scripts = "classpath:/scripts/delete.sql")
@Sql(scripts = "classpath:/scripts/conta_imports.sql")
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = BancoApplication.class)
@AutoConfigureMockMvc
public class ContaControllerTest {
	
	protected PayloadExtractor payloadExtractor;

	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	protected MockMvc mockMvc;

	protected HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private ContaService contaService;

	@BeforeEach
	public void init() {
		payloadExtractor = new PayloadExtractor(jsonMapper);
	}
	
	@Test
	public void deveRetornarTodasAsContas() throws Exception {
		mockMvc.perform(get("/contas"))
		.andExpect(status().isOk())
		.andDo(payloadExtractor)
		.andReturn();
		List<ContaDTO> contas = payloadExtractor.asListOf(ContaDTO.class, true);
		assertEquals(2, contas.size());
	}
	
	@Test
	public void deveRetornarTodasAsContasPorAgenciaENumero() throws Exception {
		mockMvc.perform(get("/contas")
				.param("agencia", "0001")
				.param("numero", "10000"))
		.andExpect(status().isOk())
		.andDo(payloadExtractor)
		.andReturn();
		List<ContaDTO> contas = payloadExtractor.asListOf(ContaDTO.class, true);
		assertEquals(1, contas.size());
		assertEquals("0001", contas.get(0).getAgencia());
		assertEquals("10000", contas.get(0).getNumero());
	}
	
	@Test
	public void deveRetornarSaldoPorAgenciaENumero() throws Exception {
		mockMvc.perform(get("/contas/saldo")
				.param("agencia", "0001")
				.param("numero", "10000"))
		.andExpect(status().isOk())
		.andDo(payloadExtractor)
		.andReturn();
		SaldoDTO saldo = payloadExtractor.as(SaldoDTO.class);
		assertEquals(1000.00D, saldo.getSaldo().doubleValue());
		assertEquals(0.0D, saldo.getSaldoFuturo().doubleValue());
	}
	
	
	@Test
	public void deveSalvarUmaNovaConta() throws Exception {
		ContaForm contaForm = new ContaForm();
		contaForm.setAgencia("0003");
		contaForm.setNumero("30000");
		
		mockMvc.perform(post("/contas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(contaForm)))
				.andExpect(status().isCreated())
				.andDo(payloadExtractor)
				.andReturn();
		

		assertEquals(3, contaRepository.count());
	}
	
	@Test
	public void naoDeveSalvarContaComMesmaAgenciaENumero() throws Exception {
		ContaForm contaForm = new ContaForm();
		contaForm.setAgencia("0001");
		contaForm.setNumero("10000");
		
		mockMvc.perform(post("/contas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(contaForm)))
				.andExpect(status().isConflict())
				.andDo(payloadExtractor)
				.andReturn();
		
		Erro erro = payloadExtractor.as(Erro.class);
		
		assertEquals("Conta com Agência: 0001 e Número: 10000 já existe", erro.getMensagemUsuario());
		assertEquals(2, contaRepository.count());
	}
	
	@Test
	public void deveRealizarDepositoNaConta() throws Exception {
		DepositoForm depositoForm = new DepositoForm();
		depositoForm.setAgencia("0001");
		depositoForm.setNumero("10000");
		depositoForm.setValor(new BigDecimal(200.0));
		
		mockMvc.perform(post("/contas/deposito")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(depositoForm)))
		.andExpect(status().isCreated())
		.andDo(payloadExtractor)
		.andReturn();
		LancamentoDTO lancamento = payloadExtractor.as(LancamentoDTO.class);
		
		SaldoDTO saldo = contaService.consultarSaldo(new ContaFilter(depositoForm.getAgencia(), depositoForm.getNumero()));
		assertEquals(1200.0D, saldo.getSaldo().doubleValue());
		assertEquals(depositoForm.getValor(), lancamento.getValor());
		assertEquals(1, lancamentoRepository.count());
	}
	
	@Test
	public void deveRealizarTransferenciaEntreContas() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0001");
		lancamentoForm.setNumeroFavorecido("10000");
		lancamentoForm.setValor(new BigDecimal(500.0));
		
		mockMvc.perform(post("/contas/transferencia")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(lancamentoForm)))
		.andExpect(status().isCreated())
		.andDo(payloadExtractor)
		.andReturn();
		LancamentoDTO lancamento = payloadExtractor.as(LancamentoDTO.class);
		
		SaldoDTO saldoFavorecido = contaService.consultarSaldo(new ContaFilter(lancamentoForm.getAgenciaFavorecido(), lancamentoForm.getNumeroFavorecido()));
		SaldoDTO saldoRemetente = contaService.consultarSaldo(new ContaFilter(lancamentoForm.getAgenciaRemetente(), lancamentoForm.getNumeroRemetente()));
	
		assertEquals(1500.0D, saldoFavorecido.getSaldo().doubleValue());
		assertEquals(1500.0D, saldoRemetente.getSaldo().doubleValue());
		assertEquals(lancamentoForm.getValor(), lancamento.getValor());
		assertEquals(1, lancamentoRepository.count());
	}
	
	@Test
	public void naoDeveRealizarTransferenciaEntreContasQuandoSaldoForInsuficiente() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0001");
		lancamentoForm.setNumeroFavorecido("10000");
		lancamentoForm.setValor(new BigDecimal(5000.0));
		
		mockMvc.perform(post("/contas/transferencia")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(lancamentoForm)))
		.andExpect(status().isConflict())
		.andDo(payloadExtractor)
		.andReturn();
		Erro erro = payloadExtractor.as(Erro.class);
		
		assertEquals("Saldo insuficiente", erro.getMensagemUsuario());
		assertEquals(0, lancamentoRepository.count());
	}
	
	@Test
	public void naoDeveRealizarTransferenciaEntreAMesmaConta() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0002");
		lancamentoForm.setNumeroFavorecido("20000");
		lancamentoForm.setValor(new BigDecimal(5000.0));
		
		mockMvc.perform(post("/contas/transferencia")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(lancamentoForm)))
		.andExpect(status().isConflict())
		.andDo(payloadExtractor)
		.andReturn();
		Erro erro = payloadExtractor.as(Erro.class);
		
		assertEquals("Conta favorecido deve ser diferente da conta remetente", erro.getMensagemUsuario());
		assertEquals(0, lancamentoRepository.count());
	}
	
	@Test
	public void deveReverterUmaTransferencia() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0001");
		lancamentoForm.setNumeroFavorecido("10000");
		lancamentoForm.setValor(new BigDecimal(500.0));
		
		LancamentoDTO lancamentoDTO =  contaService.transferir(lancamentoForm);
		
		mockMvc.perform(put("/contas/transferencia/"+lancamentoDTO.getId()+"/reverte")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andDo(payloadExtractor)
				.andReturn();	
		
		Lancamento lancamento = lancamentoRepository.findById(lancamentoDTO.getId()).orElse(null);
		
		SaldoDTO saldoFavorecido = contaService.consultarSaldo(new ContaFilter(lancamentoForm.getAgenciaFavorecido(), lancamentoForm.getNumeroFavorecido()));
		SaldoDTO saldoRemetente = contaService.consultarSaldo(new ContaFilter(lancamentoForm.getAgenciaRemetente(), lancamentoForm.getNumeroRemetente()));
	
		assertEquals(1000.0D, saldoFavorecido.getSaldo().doubleValue());
		assertEquals(2000.0D, saldoRemetente.getSaldo().doubleValue());
		assertEquals(StatusLancamento.REVERTIDO, lancamento.getStatus());
	}
	
	@Test
	public void deveRealizarAgendamentoDeTransferencia() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0001");
		lancamentoForm.setNumeroFavorecido("10000");
		lancamentoForm.setValor(new BigDecimal(600.0));
		lancamentoForm.setNumeroDeParcelas(3);
		
		mockMvc.perform(post("/contas/agendamentos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(lancamentoForm)))
		.andExpect(status().isCreated())
		.andDo(payloadExtractor)
		.andReturn();
		assertEquals(3, lancamentoRepository.count());
	}
	
	@Test
	public void deveRetornarAgendamentosPorAgenciaENumero() throws Exception {
		LancamentoForm lancamentoForm = new LancamentoForm();
		lancamentoForm.setAgenciaRemetente("0002");
		lancamentoForm.setNumeroRemetente("20000");
		lancamentoForm.setAgenciaFavorecido("0001");
		lancamentoForm.setNumeroFavorecido("10000");
		lancamentoForm.setValor(new BigDecimal(600.0));
		lancamentoForm.setNumeroDeParcelas(3);
		
		contaService.agendarTransferencia(lancamentoForm);
		
		mockMvc.perform(get("/contas/agendamentos")
				.param("agencia", "0001")
				.param("numero", "10000"))
		.andExpect(status().isOk())
		.andDo(payloadExtractor)
		.andReturn();
		List<LancamentoDTO> agendamentos = payloadExtractor.asListOf(LancamentoDTO.class);
		assertEquals(3, agendamentos.size());
	}
	
	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

	@Autowired
	protected void setConverters(HttpMessageConverter<?>[] converters) {
		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
				.findAny()
				.orElse(null);
	}	




}
