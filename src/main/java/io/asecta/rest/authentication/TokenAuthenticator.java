package io.asecta.rest.authentication;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import io.asecta.service.model.User;

public class TokenAuthenticator implements IAuthenticator {
	
	private static final long session_time = 1000 * 60 * 15; // 15 Minutes.
	private final Map<String, UserSession> tokenMap = new HashMap<>();
	private final Object lock = new Object();

	/*
	 * Returns an Accessor object based on the header's token, if present. If no
	 * token is present, an AnonymousAccessor is returned. If a token is present but
	 * it expired, null is returned. If a token is present but it is unknown, null
	 * is returned. If a token is present and it is active, its expire time is
	 * refreshed.
	 */

	@Override
	public Accessor authenticate(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader("CoAuth");
		return getAccessor(token);
	}

	@Override
	public Accessor getAccessor(String token) {
		
		System.out.println("getting accessor for " + token);
		
		System.out.println("Actice Sessions: " + tokenMap);
		
		removeExpiredSessions();
		
		if (token == null) {
			System.out.println("Token null");
			return new AnonymousAccessor();
		}
		
		UserSession session = tokenMap.get(token);
		if (session == null) {
			System.out.println("Session not found");
			return new AnonymousAccessor();
		}

		removeExpiredSessions();

		if (!session.isActive()) {
			System.out.println("Session Expired");
			synchronized (lock) {
				session.onDelete();
				tokenMap.remove(token, session);
			}
			return new AnonymousAccessor();
		}

		System.out.println("Refreshing session");
		session.refresh();
		return session.accessor;
	}

	private static String generateToken() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Records a new session for the user, as if they were logged in.
	 * If the user is named/not anonymous (instanceof User), and a session exists, returns null.
	 * If the user is anonymous, returns null.
	 * If neither of the above is true, throws an IllegalArgumentException.
	 *
	 * @param user the user
	 * @return the token for the session
	 */
	public String createSession(Accessor user) {
		if (user.isAnonymous()) {
			return null;
		}

		// Check that no session exists for the user
		// TODO add flag to avoid this check for performance?
		if (getActiveToken(user) != null) {
			return null;
		}

		synchronized (lock) {
			String token = generateToken();
			tokenMap.put(token, new UserSession(user));
			return token;
		}
	}

	public void removeSession(Accessor accessor) {
		tokenMap.remove(getActiveToken(accessor));
	}

	public String getActiveToken(Accessor user) {
		if (user == null) {
			return null;
		}

		if (user.isAnonymous()) {
			return null;
		}

		if (user instanceof User) {
			int id = ((User) user).getId();
			synchronized (lock) {
				Iterator<Map.Entry<String, UserSession>> iterator = tokenMap.entrySet().iterator();

				// noinspection WhileLoopReplaceableByForEach
				while (iterator.hasNext()) {
					Map.Entry<String, UserSession> entry = iterator.next();
					UserSession session = entry.getValue();
					if (!session.isActive()) {
						// iterator.remove();
						// Should it remove it ?
						continue;
					}

					Accessor loggedIn = session.accessor;
					if (loggedIn instanceof User && ((User) loggedIn).getId() == id) {
						return entry.getKey();
					}
				}
			}

			return null;
		}

		throw new IllegalStateException("user");
	}

	// TODO To be called on some interval
	public void removeExpiredSessions() {
		synchronized (lock) {
			tokenMap.values().removeIf(session -> {
				if (session == null) return true;
				if (!session.isActive()) {
					session.onDelete();
					return true;
				}
				return false;
			});
		}
	}

	private static class UserSession {
		private Accessor accessor;
		private long expireTime;

		UserSession(Accessor accessor) {
			this.accessor = accessor;
			refresh();
		}

		boolean isActive() {
			return expireTime > System.currentTimeMillis();
		}

		void refresh() {
			expireTime = System.currentTimeMillis() + session_time;
		}

		void onDelete() {
			// called with monitor on lock
		}
	}

}
