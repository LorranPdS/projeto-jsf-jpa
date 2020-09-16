package pacote.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import pacote.jpautil.JPAUtil;

public class DaoGeneric<T> {


	public void salvar(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		entityManager.persist(entidade);
		transaction.commit();
		entityManager.close();
	}
	
	public T merge(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		T ent = entityManager.merge(entidade);
		transaction.commit();
		entityManager.close();
		
		return ent;
	}
	
	public void deletarPorId(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(entidade);
		entityManager.createQuery("delete from "
				+ entidade.getClass().getCanonicalName() 
				+ " where id = " + id).executeUpdate();
		
		transaction.commit();
		entityManager.close();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> listarTodos(Class<T> entidade){
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<T> ent = entityManager.createQuery("from " 
				+ entidade.getName()).getResultList();
		
		transaction.commit();
		entityManager.close();
		return ent;
		
	}
	

}


















