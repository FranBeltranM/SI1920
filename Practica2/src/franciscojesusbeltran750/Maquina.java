package franciscojesusbeltran750;

import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Maquina extends AbstractPlayer {
	private int nC, nF;
	private Node[][] tablero;
	private ArrayList<Node> camino_elegido;
	private int pasos;

	/**
	 * Constructor, inicializamos el numero de filas y columnas.
	 * 
	 * @param so           Observación del estado actual.
	 * @param elapsedTimer Timer for the controller creation.
	 */
	public Maquina(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		/*
		 Cálculamos el número de filas y columnas del tablero,
		 lo calcularemos mediante la altura y el tamaño de un bloque,
		 para terminar eliminaremos 2 filas y 2 columnas,
		 que son las que coinciden con el borde de nuestro tablero.
		 */
		this.nC = stateObs.getWorldDimension().width / stateObs.getBlockSize() - 2;
		this.nF = stateObs.getWorldDimension().height / stateObs.getBlockSize() - 2;

		this.tablero = new Node[this.nF][this.nC];
		this.camino_elegido = new ArrayList<Node>();
		this.pasos = 0;
	}

	private Node[][] mapeaMundo(StateObservation datos, Node meta) {
		ArrayList<Observation>[][] mapa = datos.getObservationGrid();
		Node[][] obstaculos = new Node[nF][nC];

		for (int columna = 0; columna < this.nC; columna++) {
			for (int fila = 0; fila < this.nF; fila++) {
				obstaculos[fila][columna] = new Node(fila, columna);
				obstaculos[fila][columna].calculateHeuristic(meta);

				for (Observation obs : mapa[columna+1][fila+1])
					if (obs.itype == 0 || obs.itype == 13 || obs.itype == 4 || obs.itype == 11 || obs.itype == 10
							|| obs.itype == 7 || obs.itype == 8)
						obstaculos[fila][columna].setBlock(true);
			}
		}

		return obstaculos;
	}

	private void pintaMundo(Node[][] mundo) {
		for (int i = 0; i < this.nF; i++) {
			for (int j = 0; j < this.nC; j++) {

				if (mundo[i][j].isBlock())
					System.out.print("X");
				else
					System.out.print(" ");
			}

			System.out.println("");
		}
	}

	/**
	 * Toma la acción dependiendo del estado de la partida. Esta función es llamada
	 * cada vez que el jugador tenga que tomar una decisión.
	 * 
	 * @param stateObs     Observación del estado actual.
	 * @param elapsedTimer Timer when the action returned is due.
	 * @return Acción a realizar después de la toma de decisiones.
	 */
	public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// Hacemos uso del advance para ver venir a los coches
		stateObs.advance(Types.ACTIONS.ACTION_NIL);

		// Iniciamos el nodo inicial
		Node nodoInicial = new Node(stateObs);
		//System.out.println("NODO ACTUAL -> " + nodoInicial);

		/*
		Cuando el jugador llega a la meta, el personaje se desplaza a la (-1,-1)
		tenemos que comprobarlo antes de continuar.
		*/
		if (nodoInicial.getRow() != -1 || nodoInicial.getCol() != -1) {
			// Iniciamos una posible meta, este es el centro
			Node nodoFinal = new Node(0, 13);

			// Si no conocemos la meta todavía...
			if (stateObs.getPortalsPositions() != null) {
				int posX = (int) (stateObs.getPortalsPositions()[0].get(0).position.y / stateObs.getBlockSize()) - 1;
				int posY = (int) (stateObs.getPortalsPositions()[0].get(0).position.x / stateObs.getBlockSize()) - 1;

				nodoFinal = new Node(posX, posY);
			}

			// Realizamos el mapeo del tablero.
			this.tablero = this.mapeaMundo(stateObs, nodoFinal);

			// Podemos dibujar el tablero en cada momento.
			//this.pintaMundo(tablero);

			// Una vez tenemos nuestra posible meta, o la real, la marcamos pisable.
			this.tablero[nodoFinal.getRow()][nodoFinal.getCol()].setBlock(false);

			/* 
			Iniciamos el A* con el constructor modificado, a continuación, le pasamos nuestro
			tablero con los objetos no pisables y las casillas vacías. Las casillas vacías estarán
		 	con 0 y las no pisables con 1.
			*/
			AStar astar = new AStar(nodoInicial, nodoFinal);
			astar.setSearchArea(this.tablero);
			ArrayList<Node> path = astar.findPath();

			// Si el A* tiene solución, es decir, tenemos más de 1 nodo en nuestro posible camino...
			if (path.size() > 1) {
				Node siguiente_paso = path.get(1);
				//System.out.println("NODO SIGUIENTE -> " + siguiente_paso);
				
				if( (nodoInicial.getCol() == 12 && nodoInicial.getRow() == 12) && this.pasos>=5 ) {
					
					System.out.println("CAMINO RECORRIDO: ");
					for (Node node : this.camino_elegido) {
						System.out.println(node + ", ");
					}
					
					System.out.println("PASOS: " + this.pasos);
					this.camino_elegido = new ArrayList<Node>();
					System.out.println("");
					this.pasos=0;
				}
				
				if (siguiente_paso.getRow() != nodoInicial.getRow()) {
					if (siguiente_paso.getRow() < nodoInicial.getRow()) {
						//System.out.println("ARRIBA\n");
						this.camino_elegido.add(siguiente_paso);
						this.pasos++;
						return Types.ACTIONS.ACTION_UP;
					} else {
						//System.out.println("ABAJO\n");
						this.camino_elegido.add(siguiente_paso);
						this.pasos++;
						return Types.ACTIONS.ACTION_DOWN;
					}
				} else {
					if (siguiente_paso.getCol() > nodoInicial.getCol()) {
						//System.out.println("DERECHA\n");
						this.camino_elegido.add(siguiente_paso);
						this.pasos++;
						return Types.ACTIONS.ACTION_RIGHT;
					} else {
						//System.out.println("IZQUIERDA\n");
						this.camino_elegido.add(siguiente_paso);
						this.pasos++;
						return Types.ACTIONS.ACTION_LEFT;
					}
				}
			} else {
				//System.out.println("* ESPERO *\n");
				return Types.ACTIONS.ACTION_NIL;
			}
		} else {
			//System.out.println("* ESPERO *\n");
			return Types.ACTIONS.ACTION_NIL;
		}
	}
}
