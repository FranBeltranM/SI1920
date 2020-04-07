package franciscojesusbeltran750;

import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Arbol extends AbstractPlayer {
	private int nC, nF;
	private Node[][] tablero;
	private Node[][] objetivos;
	private ArrayList<Node> camino_elegido;
	private int pasos;

	/**
	 * Constructor, inicializamos el numero de filas y columnas.
	 * 
	 * @param so           Observación del estado actual.
	 * @param elapsedTimer Timer for the controller creation.
	 */
	public Arbol(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		/*
		 * Cálculamos el número de filas y columnas del tablero, lo calcularemos
		 * mediante la altura y el tamaño de un bloque, para terminar eliminaremos 2
		 * filas y 2 columnas, que son las que coinciden con el borde de nuestro
		 * tablero.
		 */
		this.nC = stateObs.getWorldDimension().width / stateObs.getBlockSize() - 2;
		this.nF = stateObs.getWorldDimension().height / stateObs.getBlockSize() - 2;

		this.tablero = new Node[this.nF][this.nC];
		this.camino_elegido = new ArrayList<Node>();
		this.pasos = 0;

		System.out.println("NUMERO FILAS: " + this.nF);
		System.out.println("NUMERO COLUMNAS: " + this.nC);
	}

	private Node[][] mapeaObstaculos(StateObservation datos) {
		ArrayList<Observation>[][] mapa = datos.getObservationGrid();
		Node[][] obstaculos = new Node[nF][nC];

		for (int columna = 0; columna < this.nC; columna++) {
			for (int fila = 0; fila < this.nF; fila++) {
				obstaculos[fila][columna] = new Node(fila, columna);
				// obstaculos[fila][columna].calculateHeuristic(meta);

				for (Observation obs : mapa[columna + 1][fila + 1])
					if (obs.itype == 0)
						obstaculos[fila][columna].setBlock(true);
			}
		}

		return obstaculos;
	}

	private Node[][] mapeaObjetivos(StateObservation datos) {
		ArrayList<Observation>[][] mapa = datos.getObservationGrid();
		Node[][] objetivos = new Node[nF][nC];

		for (int columna = 0; columna < this.nC; columna++) {
			for (int fila = 0; fila < this.nF; fila++) {
				// obstaculos[fila][columna].calculateHeuristic(meta);
				objetivos[fila][columna] = new Node(fila, columna);

				for (Observation obs : mapa[columna + 1][fila + 1])
					if (obs.itype == 6)
						objetivos[fila][columna].setBlock(true);
			}
		}

		return objetivos;
	}

	private void pintaMundo(Node[][] obstaculos, Node[][] objetivos) {
		for (int i = 0; i < this.nF; i++) {
			for (int j = 0; j < this.nC; j++) {
				if (objetivos[i][j].isBlock() || obstaculos[i][j].isBlock()) {
					if (objetivos[i][j].isBlock())
						System.out.print("I");
					else
						System.out.print("X");
				}
				else
					System.out.print("_");
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
		System.out.println("NODO ACTUAL -> " + nodoInicial);

		/*
		 * Cuando el jugador llega a la meta, el personaje se desplaza a la (-1,-1)
		 * tenemos que comprobarlo antes de continuar.
		 */

		// Realizamos el mapeo del tablero.
		this.tablero = this.mapeaObstaculos(stateObs);
		this.objetivos = this.mapeaObjetivos(stateObs);
		
		// Podemos dibujar el tablero en cada momento.
		this.pintaMundo(tablero, objetivos);

		return ACTIONS.ACTION_NIL;
	}
}
