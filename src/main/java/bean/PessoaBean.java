package bean;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import dao.DaoGeneric;
import entidade.Pessoa;
import repository.IDaoPessoa;
import repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private Pessoa pessoa = new Pessoa();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	private Part arquivoFoto;

	public String salvar() throws IOException {

		if (arquivoFoto == null) {
			pessoa.getFotoIconBase64();
		} else {

			/*----------------INÍCIO IMAGEM------------------*/

			// Processando imagem
			byte[] imagemByte = getByte(arquivoFoto.getInputStream());

			// Salva a imagem original
			pessoa.setFotoIconBase64Original(imagemByte);

			// Transformando em bufferedImage
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));

			// Pegando o tipo da imagem
			int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();

			int largura = 200;
			int altura = 200;

			// Criando a nossa miniatura
			BufferedImage resizedImage = new BufferedImage(largura, altura, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(bufferedImage, 0, 0, largura, altura, null);
			g.dispose(); // Grava a imagem

			// Escrever novamente a imagem, mas em tamanho menor
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String extensao = arquivoFoto.getContentType().split("\\/")[1]; // Retorna image/png
			ImageIO.write(resizedImage, extensao, baos);

			String miniImagem = "data:" + arquivoFoto.getContentType() + ";base64,"
					+ DatatypeConverter.printBase64Binary(baos.toByteArray());

			// Processando a imagem
			pessoa.setFotoIconBase64(miniImagem);
			pessoa.setExtensao(extensao);
		}

		pessoa = daoGeneric.salvar(pessoa);
		mostrarMsg("Salvo com sucesso!");
		pessoa = new Pessoa();
		carregarPessoas();
		return "";
	}

	public String novo() {
		pessoa = new Pessoa();
		return "";
	}

	public String remover() {
		daoGeneric.deletarPorId(pessoa);
		mostrarMsg("Removido com sucesso!");
		pessoa = new Pessoa();
		carregarPessoas();
		return "";
	}

	public boolean permissaoAcesso(char acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("loginUsuario");
		return pessoaUser.getPerfil().equals(acesso);
	}

	public String logar() {
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());

		if (pessoaUser != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("loginUsuario", pessoaUser);

			return "cadastro.jsf";
		}
		return "index.jsf";
	}

	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("loginUsuario");

		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		request.getSession().invalidate();
		return "index.jsf";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.listarTodos(pessoa);
	}

	public void pesquisarCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}

			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	private byte[] getByte(InputStream is) throws IOException {

		int length;
		int size = 1024;
		byte[] buf = null;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			length = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];

			while ((length = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, length);
			}
			buf = bos.toByteArray();
		}
		return buf;
	}

	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String fileDownloadId = params.get("fileDownloadId");
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();

		// Setando o cabeçalho e dizendo o tipo de arquivo
		response.addHeader("Content-Disposition", "attachment; filename=download." + pessoa.getExtensao());

		response.setContentType("application/octet-stream"); // stream é relativo a mídia, foto
		response.setContentLength(pessoa.getFotoIconBase64Original().length); // Tamanho
		response.getOutputStream().write(pessoa.getFotoIconBase64Original()); // Seta os dados
		response.getOutputStream().flush(); // Confirma a resposta do fluxo de dados
		FacesContext.getCurrentInstance().responseComplete(); // Diz que é a resposta completa

	}
	
	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public IDaoPessoa getiDaoPessoa() {
		return iDaoPessoa;
	}

	public void setiDaoPessoa(IDaoPessoa iDaoPessoa) {
		this.iDaoPessoa = iDaoPessoa;
	}

	public Part getArquivoFoto() {
		return arquivoFoto;
	}

	public void setArquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}

}
