package io.asecta.rest.router.requesthandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.asecta.rest.router.Status;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;

public class RequestBody {

	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private Map<String, Object> dataMap;
	private boolean allowNull = false;

	@SuppressWarnings("unchecked")
	public RequestBody(String body) throws InvalidBodyException {
		try {
			dataMap = GSON.fromJson(body, Map.class);
		} catch (Exception e) {
			dataMap = new HashMap<>();
			dataMap.put("raw", body);
		}
	}

	private RequestBody(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> type) throws InvalidBodyException {
		try {
			return (T) dataMap.get(key);
		} catch (ClassCastException e) {
			if (allowNull) {
				return null;
			}
			throw new InvalidBodyException(Status.BAD_REQUEST, "Invalid POST body. *" + key + "* is of the wrong type");
		} catch (NullPointerException e) {
			if (allowNull) {
				return null;
			}
			throw new InvalidBodyException(Status.BAD_REQUEST, "Invalid POST body. *" + key + "* isn't defined");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key, Class<T> type) throws InvalidBodyException {
		return (List<T>) get(key, List.class);
	}

	public List<String> getStringList(String key) throws InvalidBodyException {
		return getList(key, String.class);
	}

	@SuppressWarnings("unchecked")
	public List<RequestBody> getSectionList(String key) throws InvalidBodyException {
		try {
			return getList(key, Object.class).stream().map(o -> new RequestBody((Map<String, Object>) o))
					.collect(Collectors.toList());
		} catch (Exception e) {
			if (allowNull) {
				return null;
			}
			throw new InvalidBodyException(Status.BAD_REQUEST, "Invalid POST body. *" + key + "* is of the wrong type");
		}
	}

	public int getInt(String key) throws InvalidBodyException {
		try {
			return (int) Math.floor(get(key, Double.class));
		} catch (Exception e) {
			return -1; // this is bad if -1 is usable input, but it won't be a problem in this project. make an exception at some point
		}
	}

	public String getString(String key) throws InvalidBodyException {
		return get(key, String.class);
	}

	public RequestBody getSection(String key) throws InvalidBodyException {
		return new RequestBody(getMap(key));
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String key) throws InvalidBodyException {
		try {
			return (Map<String, Object>) get(key, Object.class);
		} catch (Exception e) {
			if (allowNull) {
				return null;
			}
			throw new InvalidBodyException(Status.BAD_REQUEST, "Invalid POST body. *" + key + "* is of the wrong type");
		}
	}

	public void allowNull() {
		allowNull = true;
	}

	public void denyNull() {
		allowNull = false;
	}
}
