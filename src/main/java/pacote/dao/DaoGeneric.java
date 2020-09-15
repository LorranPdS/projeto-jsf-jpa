package pacote.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import pacote.hibernate.HibernateUtil;

public class DaoGeneric<T> {

	private EntityManager entityManager = HibernateUtil.getEntityManager();

	public void salvar(T entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(entidade);
		transaction.commit();
	}

	public T pesquisar1(T entidade) {
		Object id = HibernateUtil.getPrimaryKey(entidade);

		@SuppressWarnings("unchecked")
		T ent = (T) entityManager.find(entidade.getClass(), id);

		return ent;
	}

	public T pesquisar2(Long id, Class<T> entidade) {
		T ent = entityManager.find(entidade, id);

		return ent;
	}

	public T atualizar(T entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		T userAtualizado = entityManager.merge(entidade);
		transaction.commit();
		return userAtualizado;
	}

	public void deletarPorId(T entidade) {
		Object id = HibernateUtil.getPrimaryKey(entidade);
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		entityManager.createNativeQuery("delete from " 
				+ entidade.getClass().getSimpleName().toLowerCase()
				+ " where id = " + id).executeUpdate();
		
		transaction.commit();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> listar(Class<T> entidade){
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<T> list = entityManager.createQuery("from " 
				+ entidade.getName()).getResultList();
		
		transaction.commit();
		return list;		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

}


















