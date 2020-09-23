package repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import entidade.Lancamento;
import jpautil.JPAUtil;

public class IDaoLancamentoImpl implements IDaoLancamento {

	@SuppressWarnings("unchecked")
	@Override
	public List<Lancamento> consultar(Long codUser) {
		
		List<Lancamento> lista = null;

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		lista = entityManager.createQuery("from Lancamento where usuario.id = " 
				+ codUser).getResultList();
		
		transaction.commit();
		entityManager.close();
	
		return lista;
	}

}
