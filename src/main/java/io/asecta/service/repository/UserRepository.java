package io.asecta.service.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.rest.repository.CsRepository;
import io.asecta.service.model.User;

public class UserRepository extends CsRepository<User> {

	private CriteriaQuery<User> selectAll;
	private CriteriaQuery<User> selectByEmail;
	private CriteriaDelete<User> deleteByEmail;

	public UserRepository(@Inject SessionFactory factory) {
		super(factory, "users", User.class);

		this.selectAll = selectAll("id", "id");
		this.selectByEmail = queryByParameter("email", "email");
		this.deleteByEmail = makeDelete((builder, delete, table) -> {
			delete.where(fieldEqualsParameter(builder, table, "email", "email"));
		});
	}

	public List<User> getAllUsers(int start, int count) {
		try (Session session = factory.openSession()) {
			return session.createQuery(selectAll).setParameter(1, start).setMaxResults(count).list();
		} catch (Exception e) {
			return null;
		}
	}

	public User getItemByEmail(String email) {
		try (Session session = factory.openSession()) {
			Transaction tx = session.beginTransaction();

			Query<User> query = session.createQuery(selectByEmail);
			query.setParameter("email", email);
			query.setMaxResults(1);

			User user = query.getSingleResult();
			tx.commit();
			return user;
		} catch (NoResultException ex) {
			return null;
		} catch (PersistenceException ex) {
			return null;
		}
	}

	public boolean deleteItemByEmail(String email) {
		Transaction tx = null;
		try (Session session = factory.openSession()) {
			tx = session.beginTransaction();
			int numDeleted = session.createQuery(deleteByEmail).setParameter(1, email).executeUpdate();
			tx.commit();
			return numDeleted > 0;
		} catch (PersistenceException ex) {
			if (tx != null) {
				tx.rollback();
			}
			return false;
		}
	}

}