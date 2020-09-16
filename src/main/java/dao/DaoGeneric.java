package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import jpautil.JPAUtil;

public class DaoGeneric<T> {

	public T salvar(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		T ent = entityManager.merge(entidade);
		transaction.commit();
		entityManager.close();
		
		return ent;
	}
	
	public void excluir(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(entidade);
		entityManager.createQuery("delete from " 
		+ entidade.getClass().getCanonicalName() + " where id = " + id).executeUpdate();
		
		transaction.commit();
		entityManager.close();		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> listarTodos(Class<T> entidade){
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<T> result = entityManager.createQuery("from " 
		+ entidade.getName()).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return result;
	}	
}