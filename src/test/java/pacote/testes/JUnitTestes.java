package pacote.testes;

import org.junit.Test;

import pacote.hibernate.HibernateUtil;

public class JUnitTestes {

	@Test
	public void testeHibernateUtil() {
		HibernateUtil.getEntityManager();
	}
}

/* FUNCIONAMENTO
 * 1. Criei uma estrutura front-end e back-end básica neste momento em condições
 * de exibir uma mensagem simples na página index ao subir o servidor,
 * bem como conseguir conectar ao BD usando o JUnit
 * 
 * 2. Criada a estrutura básica, minha preocupação não será desenvolver o front-end
 * e o back-end juntos, mas sim me preocupar primeiramente com os códigos de
 * persistência, os quais serão desenvolvidos na próxima versão, e somente depois
 * de prontos, começarei a desenvolver o front-end com o back-end restante
 * */