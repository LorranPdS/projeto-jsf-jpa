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
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import dao.DaoGeneric;
import entidade.Cidades;
import entidade.Estados;
import entidade.Pessoa;
import jpautil.JPAUtil;
import repository.IDaoPessoa;
import repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();

	private List<SelectItem> estados;
	private List<SelectItem> cidades;
	private Part arquivoFoto;

	public String salvar() throws IOException {
		
		/*Foi criada uma condição no caso do Editar para evitar o NullPointerException
		 * caso não tenha imagem salva no BD. Ainda não está perfeito porque gostaria
		 * de mostrar, ao clicar no Editar, que no componente do File na tela
		 * retorne o nome da imagem salva*/
		if(arquivoFoto == null) {
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
		int type = bufferedImage.getType() == 0 ?
				BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
		
		int largura = 200;
		int altura = 200;
		
		//Criando a nossa miniatura
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
		
		/*----------------FINAL IMAGEM------------------*/
		
		pessoa = daoGeneric.salvar(pessoa);
		carregarPessoas();
		mostrarMsg("Salvo com sucesso!");
		return "";
	}
	
	// Método 'receita de bolo' para converter inputStream de um arquivo para array de bytes
	private byte[] getByte(InputStream is) throws IOException {
		int length;
		int size = 1024;
		byte[] buf = null;
		
		if(is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			length = is.read(buf, 0, size);
		}
		else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			
			while((length = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, length);
			}
			buf = bos.toByteArray();
		}
		return buf;
	}
	
	// Método receita de bolo para download
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		
		String fileDownloadId = params.get("fileDownloadId");
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		
		// Setando o cabeçalho e dizendo o tipo de arquivo
		response.addHeader("Content-Disposition", "attachment; filename=download."
				+ pessoa.getExtensao());
		
		response.setContentType("application/octet-stream"); // stream é relativo a mídia, foto
		response.setContentLength(pessoa.getFotoIconBase64Original().length); // Tamanho
		response.getOutputStream().write(pessoa.getFotoIconBase64Original()); // Seta os dados
		response.getOutputStream().flush(); // Confirma a resposta do fluxo de dados
		FacesContext.getCurrentInstance().responseComplete(); // Diz que é a resposta completa
		
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	public String novo() {
		pessoa = new Pessoa();
		return "";
	}

	public String remover() {
		daoGeneric.deletarPorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
		return "";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.listarTodos(pessoa);
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

	public boolean permissaoAcesso(char acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("loginUsuario");

		return pessoaUser.getPerfil().equals(acesso);
	}

	@SuppressWarnings("unchecked")
	public void carregarCidades(AjaxBehaviorEvent event) {
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		if (estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemCidade = new ArrayList<SelectItem>();

			for (Cidades c : cidades) {
				selectItemCidade.add(new SelectItem(c, c.getNome()));
			}
			setCidades(selectItemCidade);
		}
	}

	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("loginUsuario");

		HttpServletRequest servRequest = (HttpServletRequest) externalContext.getRequest();
		servRequest.getSession().invalidate();
		return "index.jsf";
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
			mostrarMsg("CEP não encontrado!");
		}
	}

	@SuppressWarnings("unchecked")
	public void editar() {
		if (pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();

			for (Cidades c : cidades) {
				selectItemsCidade.add(new SelectItem(c, c.getNome()));
			}
			setCidades(selectItemsCidade);
		}
	}

	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listarEstados();
		return estados;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public List<SelectItem> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
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

	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}

}
