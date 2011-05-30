package common;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class RenderJsonWithExclusion extends Result {
	String json;

	public RenderJsonWithExclusion(Object o) {
		GsonBuilder gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation();
		json = gson.create().toJson(o);
	}

	public RenderJsonWithExclusion(Object o, JsonSerializer<?>... adapters) {
		GsonBuilder gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation();
		for (Object adapter : adapters) {
			Type t = getMethod(adapter.getClass(), "serialize")
					.getParameterTypes()[0];
			;
			gson.registerTypeAdapter(t, adapter);
		}
		json = gson.create().toJson(o);
	}

	public RenderJsonWithExclusion(String jsonString) {
		json = jsonString;
	}

	public void apply(Request request, Response response) {
		try {
			setContentTypeIfNotSet(response, "application/json; charset=utf-8");
			response.out.write(json.getBytes("utf-8"));
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	// 
	static Method getMethod(Class<?> clazz, String name) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
}