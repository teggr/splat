package splat.core;

public interface ProxyService {

	void start(int port, String contextPath);

	void remove(String contextPath);

}
