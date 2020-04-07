package franciscojesusbeltran750;

import java.util.ArrayList;
 /* https://repositorio.upct.es/bitstream/handle/10317/3755/pfc5621.pdf;jsessionid=DF11EB113941A8792BBD1CADEC2CAE1C?sequence=1 */
class Nodo {
	String nombre = null;
	String respuesta = null;
	Nodo yes = null;
	Nodo no = null;
	Hoja hijoI = null, hijoD = null;
	ArrayList<Hoja> hijos;
	boolean nodo_final = true;
	
	public Nodo(String p, String r, Nodo y, Nodo n) {
		this.pregunta = p;
		this.respuesta = r;
		this.yes = y;
		this.no = n;
		this.hijos = new ArrayList<Hoja>();
	}
	
	public void setHijos(Hoja h1, Hoja h2) {
		this.hijoI = h1;
		this.hijoD = h2;
		this.hijos.add(h1);
		this.hijos.add(h2);
	}
	
	public ArrayList<Hoja> getHijos() {
		return this.hijos;
	}
	
	
}
