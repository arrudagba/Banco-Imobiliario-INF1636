package controller.observer;

public interface ObservadoApi {
	void registraObservador(String evento, ObservadorApi o);   
    void removeObservador(String evento, ObservadorApi o);     
    void notifica(String evento);
}

