package io.asecta.rest.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import io.asecta.core.dependencyinjection.annotations.Inject;

public abstract class CsRepository<TItem extends Identifiable> {

	protected final SessionFactory factory;
	protected final String tableName;
	protected final Class<TItem> itemClass;

	protected CsRepository(@Inject SessionFactory factory, String tableName, Class<TItem> clazz) {
		this.factory = factory;
		this.tableName = tableName;
		this.itemClass = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * Get the item by the given ID, if it exists.
	 *
	 * @param id the ID
	 * @return the item if it exists, otherwise null
	 */
	public TItem getItemById(Serializable id) {
		try (Session session = factory.openSession()) {
			return session.get(itemClass, id);
		}
	}

	/**
	 * Makes the given item a persistent one
	 * @param item the item that should persist
	 */
	public void saveItem(TItem item) {
		Objects.requireNonNull(item);
		try (Session session = factory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.saveOrUpdate(item);
//			session.persist(item);
			transaction.commit();
			session.close();
		}
	}
	
//	public void persist(TItem item) {
//		try (Session session = factory.openSession()) {
//			Transaction transaction = session.beginTransaction();
//			transaction.commit();
//			session.close();
//		}
//
//	}

	public boolean saveItemIfNotExists(TItem item) {
		Objects.requireNonNull(item);
		try (Session session = factory.openSession()) {
			if (session.get(itemClass, item.getId()) != null) {
				return false;
			}

			Transaction transaction = session.beginTransaction();
			session.persist(item);
			transaction.commit();
			session.close();

			return true;
		}
	}

	public void deleteItem(TItem item) {
		Objects.requireNonNull(item);
		try (Session session = factory.openSession()) {
			session.delete(item);
		}
	}

	protected CriteriaQuery<TItem> makeQuery(CriteriaConfigurer<TItem, CriteriaQuery<TItem>> configurer) {
		CriteriaBuilder builder = factory.getCriteriaBuilder();
		CriteriaQuery<TItem> query = builder.createQuery(itemClass);
		Root<TItem> table = query.from(itemClass);
		configurer.configure(builder, query, table);
		return query;
	}

	protected CriteriaUpdate<TItem> makeUpdate(CriteriaConfigurer<TItem, CriteriaUpdate<TItem>> configurer) {
		CriteriaBuilder builder = factory.getCriteriaBuilder();
		CriteriaUpdate<TItem> update = builder.createCriteriaUpdate(itemClass);
		Root<TItem> table = update.from(itemClass);
		configurer.configure(builder, update, table);
		return update;
	}

	protected CriteriaDelete<TItem> makeDelete(CriteriaConfigurer<TItem, CriteriaDelete<TItem>> configurer) {
		CriteriaBuilder builder = factory.getCriteriaBuilder();
		CriteriaDelete<TItem> delete = builder.createCriteriaDelete(itemClass);
		Root<TItem> table = delete.from(itemClass);
		configurer.configure(builder, delete, table);
		return delete;
	}

	protected Expression<Boolean> fieldEqualsParameter(CriteriaBuilder builder, Root<TItem> table, String fieldName,
			String parameterName) {
		Path<?> path = table.get(fieldName);
		ParameterExpression<?> parameter = builder.parameter(path.getJavaType(), parameterName);
		return builder.equal(path, parameter);
	}

	protected CriteriaQuery<TItem> selectAll(String fieldName, String parameterName) {
		return makeQuery((builder, criteriaQuery, table) -> {
			Path<Integer> idPath = table.get(fieldName);
			criteriaQuery.select(table);
			criteriaQuery.where(
					builder.greaterThanOrEqualTo(idPath, builder.parameter(idPath.getJavaType(), parameterName)));
		});
	}

	protected CriteriaQuery<TItem> queryByParameter(String fieldName, String parameterName) {
		return makeQuery((builder, criteriaQuery, table) -> {
			criteriaQuery.select(table);
			criteriaQuery.where(fieldEqualsParameter(builder, table, fieldName, parameterName));
		});
	}

	protected <T> List<T> getCollectionByParameterSelection(CriteriaQuery<T> query, Object parameter) {
		try (Session session = factory.openSession()) {
			return session.createQuery(query).setParameter(1, parameter).list();
		} catch (PersistenceException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	protected interface CriteriaConfigurer<TItem, TQuery extends CommonAbstractCriteria> {
		void configure(CriteriaBuilder builder, TQuery query, Root<TItem> table);
	}

}
