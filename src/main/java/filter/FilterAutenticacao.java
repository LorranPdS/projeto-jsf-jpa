package filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import entidade.Pessoa;
import jpautil.JPAUtil;

@WebFilter(urlPatterns = { "/*" })
public class FilterAutenticacao implements Filter {

	@Inject
	private JPAUtil jpaUtil;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();

		Pessoa loginUsuario = (Pessoa) session.getAttribute("loginUsuario");
		String url = req.getServletPath();

		if (!url.equalsIgnoreCase("/index.jsf") && loginUsuario == null) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsf");
			dispatcher.forward(request, response);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		jpaUtil.getEntityManager();
	}

}
