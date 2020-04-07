package franciscojesusbeltran750;

public class Hoja {
	String nombre = null;
	Nodo padre = null;
	
	public Hoja(Nodo p, String n) {
		this.padre = p;
		this.nombre = n;
	}

	public String getAccion() {
		return this.nombre;
	}
	
	public Nodo getPadre() {
		return this.padre;
	}
	
	public void setPadre(Nodo p) {
		this.padre = p;
	}
}
