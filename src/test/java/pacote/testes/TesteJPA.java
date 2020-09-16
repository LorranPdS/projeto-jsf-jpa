package pacote.testes;

import javax.persistence.Persistence;

public class TesteJPA {

	public static void main(String[] args) {
		Persistence.createEntityManagerFactory("projeto-jsf-jpa");
	}
}
