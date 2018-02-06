package pa.generators;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.io.PajekNetWriter;

public class MainCalibCoefClastPA {
	static Factory<Integer> vertexFactory = new Factory<Integer>() { // фабрика
																		// для
																		// создания
																		// вершин
		int i = 0;

		public Integer create() {
			return new Integer(i++);
		}
	};
	static Factory<Integer> edgeFactory = new Factory<Integer>() { // фабрика
																	// для
																	// создания
																	// ребер
		int i = 0;

		public Integer create() {
			return new Integer(i++);
		}
	};
	private static Graph graph;

	public static void main(String[] args) throws IOException {
		double[] r_Diad = new double[] {
				0,
				0.70156
				,0.16244
				,0.02582
				,0.05388
				,0.00060
				,0.00741
				,0.00380
				,0.00077
				,0.00526
				,0.00060
				,0.00196
				,0.00375
				,0.00239
				,0.00153
				,0.00206
				,0.00191
				,0.00154
				,0.00212
				,0.00085
				,0.01976
		};
		
		double[] r_Diad1 = new double[] {
				0,
				0.37559
				,0.46269
				,0.11616
				,0.02199
				,0.00145
				,0.00513
				,0.00212
				,0.00000
				,0.00383
				,0.00000
				,0.00000
				,0.00373
				,0.00120
				,0.00000
				,0.00003
				,0.00000
				,0.00000
				,0.00041
				,0.00005
				,0.00000
				,0.00009
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00114
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00439
		};
		
		double[] r_nonDiad = new double[] {
				0,
				0.37009
				,0.45798
				,0.10600
				,0.03310
				,0.00214
				,0.00819
				,0.00384
				,0.00000
				,0.00386
				,0.00000
				,0.00000
				,0.00380
				,0.00173
				,0.00000
				,0.00000
				,0.00000
				,0.00000
				,0.00093
				,0.00047
				,0.00788

		};
		GenCalibCoefClastPA genBA = new GenCalibCoefClastPA(vertexFactory, edgeFactory, r_Diad,r_nonDiad, paDiad);
		Graph graph = genBA.evolve((int)(25000*(0.578458+1)), seed_graph());

		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());
		System.out.println("m="+graph.getEdgeCount()/(double)graph.getVertexCount());
		int k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		int[] degrees=getNodesDegrees(graph, k_max+1);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}
	
		int[][] mass = getQMatrix(graph, 40);
		File logFile = new File("diad_out3.txt");
		FileWriter writeFile = new FileWriter(logFile);
		int count = 0;
		for (int i = 1; i < mass.length; i++) {
			for (int j = 1; j < mass.length; j++) {
				  writeFile.write(String.format("%.8f", mass[i][j] / 2. / (double) graph.getEdgeCount()) + " ");
			}
			writeFile.write("\n");
		}
		writeFile.close();
		saveGraph(graph, "diad.net");
		

	}
	public static void mainOmGTUComp(String[] args) throws IOException {
		graph = getNetObject("omgtu_oneComponent.net",0);

		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());
		int k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		int[] degrees=getNodesDegrees(graph, k_max+1);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}

		int[][] mass = getQMatrix(graph, 40);
		File logFile = new File("vk_out.txt");
		FileWriter writeFile = new FileWriter(logFile);
		int count = 0;
		for (int i = 1; i < mass.length; i++) {
			for (int j = 1; j < mass.length; j++) {
				  writeFile.write(String.format("%.8f", mass[i][j] / 2. / (double) graph.getEdgeCount()) + " ");
			}
			writeFile.write("\n");
		}
		writeFile.close();
		
	}

	// функция для получения массива встречаемости узлов с заданной степенью
	// связности
	public static <V> int[] getNodesDegrees(Graph<V, ?> graph, int length) {
		Iterator<V> it = graph.getVertices().iterator();
		int[] distr = new int[length];
		while (it.hasNext()) {
			V node = it.next();
			int n = graph.degree(node);
			if (n < length)
				distr[n] = distr[n] + 1;
		}
		return distr;
	}

	// загрузка графа
	public static Graph<Integer, Integer> getNet(String fileName) {
		// System.out.println(fileName);
		Graph graph = new UndirectedSparseGraph();
		PajekNetReader<Graph<Integer, Integer>, Integer, Integer> pnr;
		try {
			pnr = new PajekNetReader<Graph<Integer, Integer>, Integer, Integer>(vertexFactory, edgeFactory);
			File file = new File(fileName);
			pnr.load(fileName, graph);

		} catch (IOException e5) {
			System.out.println("IOException!!!!!!!!!!!!!!!!!!");
		}
		// System.out.println("Nodes num=" + graph.getVertexCount());
		// System.out.println("Edges num=" + graph.getEdgeCount());
		return graph;
	}
	public static Graph getNetObject(String fileName,final int begin) {
		Factory<Object> vertexFactory = new Factory<Object>() {
			int count = begin;

			public Object create() {
				return new Integer(count++);
			}
		};
		Factory<Object> edgeFactory = new Factory<Object>() {
			public Object create() {
				return new Object();
			}
		};

		// System.out.println(fileName);
		Graph graph = new UndirectedSparseGraph();
		PajekNetReader<Graph<Object, Object>, Object, Object> pnr;
		try {
			pnr = new PajekNetReader<Graph<Object, Object>, Object, Object>(vertexFactory, edgeFactory);
			File file = new File(fileName);
			pnr.load(fileName, graph);

		} catch (IOException e5) {
			System.out.println("IOException!!!!!!!!!!!!!!!!!!");
		}
		 System.out.println("Nodes num=" + graph.getVertexCount());
		 System.out.println("Edges num=" + graph.getEdgeCount());
		return graph;
	}
	public static void main4(String[] args) throws IOException {
		Graph g1 = getNetObject("AER.net",0);
		Graph g2 = getNetObject("LPA.net",g1.getVertexCount()+1);
		for (Object v2 : g2.getVertices()) {
			g1.addVertex(v2);
		}
		for (Object e2 : g2.getEdges()) {
			Pair p = g2.getEndpoints(e2);
			g1.addEdge(e2,p.getFirst() , p.getSecond());
		}
		graph =g1;
		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());
		int k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		int[] degrees=getNodesDegrees(graph, k_max+1);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}

		int[][] mass = getQMatrix(graph, 40);
		File logFile = new File("my_out4.txt");
		FileWriter writeFile = new FileWriter(logFile);
		int count = 0;
		for (int i = 1; i < mass.length; i++) {
			for (int j = 1; j < mass.length; j++) {
				  writeFile.write(String.format("%.8f", mass[i][j] / 2. / (double) graph.getEdgeCount()) + " ");
			}
			writeFile.write("\n");
		}
		writeFile.close();

	}
	public static Set<Set<Integer>> getClusters(Graph<Integer,Integer> graph) {

        Set<Set<Integer>> clusterSet = new HashSet<Set<Integer>>();

        HashSet<Integer> unvisitedVertices = new HashSet<Integer>(graph.getVertices());

        while (!unvisitedVertices.isEmpty()) {
        	Set<Integer> cluster = new HashSet<Integer>();
        	Integer root = unvisitedVertices.iterator().next();
            unvisitedVertices.remove(root);
            cluster.add(root);

            Buffer<Integer> queue = new UnboundedFifoBuffer<Integer>();
            queue.add(root);

            while (!queue.isEmpty()) {
            	Integer currentVertex = queue.remove();
                Collection<Integer> neighbors = graph.getNeighbors(currentVertex);

                for(Integer neighbor : neighbors) {
                    if (unvisitedVertices.contains(neighbor)) {
                        queue.add(neighbor);
                        unvisitedVertices.remove(neighbor);
                        cluster.add(neighbor);
                    }
                }
            }
            clusterSet.add(cluster);
        }
        return clusterSet;
    }
	
	static PrefferentialAttachment paBA = new PrefferentialAttachment() {
		@Override
		public double f(int k) {
			return k;
		}

	
	};
	static PrefferentialAttachment paDiad = new PrefferentialAttachment() {
		
		double[] d={
				0
				,0.674653941
				,0.144005349
				,0.273933959
				,1.007415174
				,1.604742744
				,1.760445167
				,1.774334517
				,1.292659724
				,1.315724876
				,0.834211467
				,0.270279827
				,0.408893198
				,0.497910592
				,0.241411517
				,0.230069662
				,0.40512869
				,0.514773552
				,1.134053195
				,1.17313742
				,17.83687722
				,24.51771137
				,25.25781302
				,26.44157595
				,27.59235747
				,28.74558277
				,29.8986295
				,31.05169084
				,32.20475243
				,33.35781518
				,34.51087891
				,35.66394352
				,36.81700893
				,37.97007506
				,39.12314185
				,40.27620924
				,41.42927718
				,42.58234562
				,43.73541453
				,44.88848386
				,46.04155359
				,47.19462368
				,48.3476941
				,49.50076485
				,50.65383588
				,51.80690719
				,52.95997875
				,54.11305055
				,55.26612257
				,56.41919481
				,57.57226723

		};
		@Override
		public double f(int k) {
			if(d.length>k)return d[k];
			return 1.153015732*k;
		}

	};
	static PrefferentialAttachment paDiad1 = new PrefferentialAttachment() {
		
		double[] d={
				0,
				0
				,0.084150036
				,0.939106202
				,2.073650781
				,3.14371131
				,4.252688815
				,4.958476716
				,5.416925206
				,6.458640288
				,6.976252121
				,7.387147488
				,8.913033395
				,9.964028431
				,10.55667444
				,11.10275572
				,11.60537919
				,12.0748273
				,12.80746097
				,13.31158445
				,13.73066076
				,14.20687714
				,14.57933888
				,14.91480061
				,15.22018568
				,15.4951417
				,15.73952693
				,15.95426362
				,16.13872676
				,16.29088378
				,16.41247912
				,16.50419566
				,16.56288724
				,19.46616361
				,19.87421939
				,20.03700016
				,20.18053972
				,20.29858869
				,20.39123196
				,20.45838402
				,37.04188699
				,39.23062645
				,40.15409713
				,41.11169956
				,42.06838242
				,43.02509007
				,43.98179708
				,44.93850414
				,45.89521123
				,46.85191835
				,47.8086255
		};
		@Override
		public double f(int k) {
			if(d.length>k)return d[k];
			return 0.956687661486075*k;
		}

	};

	static Graph seed_graph() {
		Graph gr = new UndirectedSparseMultigraph<Integer, Integer>();
		for (int i = -1; i > -23; i--) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}
		int l = -1;
		Object[] mass = gr.getVertices().toArray();
		for (int i = 0; i < mass.length - 1; i++)
			for (int j = i + 1; j < mass.length; j++)
				if (i != j)
					gr.addEdge(new Integer(l--), (Integer) mass[i], (Integer) mass[j]);

		return gr;
	}
	public static void main2(String[] args) {
		Graph<Integer,Integer> g=getNet("AER.net");
		System.out.println("V:" + g.getVertexCount());
		System.out.println("E:" + g.getEdgeCount());
		Set<Set<Integer>> cl=getClusters(g);
		for (Set<Integer> set : cl) {
			System.out.println( set.size());
		}
		System.out.println();
		
	}
	public static void mainLPA(String[] args) throws IOException{
		double[] r_BA = new double[] {
				0.,		
0.35572,
0.13646,
0.07975,
0.05481,
0.04107,
0.03249,
0.02667,
0.02248,
0.01935,
0.01692,
0.01499,
0.01342,
0.01212,
0.01104,
0.01011,
0.00932,
0.00863,
0.00803,
0.00750,
0.00703,
0.00661,
0.00623,
0.00589,
0.00558,
0.00530,
0.00504,
0.00481,
0.00459,
0.00440,
0.00421,
0.00404,
0.00388,
0.00373,
0.00360,
0.00347,
0.00335,
0.00323,
0.00313,
0.00303,
0.00293,
0.00284,
0.00276,
0.00267,
0.00260,
0.00253,
0.00246,
0.00239,
0.00233,
0.00227,
0.00221
		};
		GenPA genBA = new GenPA(vertexFactory, edgeFactory, r_BA, paBA);
		Graph graph = genBA.evolve(65000-7, seed_graph());
		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());
		int k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		int[] degrees=getNodesDegrees(graph, k_max+1);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}

		int[][] mass = getQMatrix(graph, 40);
		File logFile = new File("my_out3.txt");
		FileWriter writeFile = new FileWriter(logFile);
		int count = 0;
		for (int i = 1; i < mass.length; i++) {
			for (int j = 1; j < mass.length; j++) {
				  writeFile.write(String.format("%.8f", mass[i][j] / 2. / (double) graph.getEdgeCount()) + " ");
			}
			writeFile.write("\n");
		}
		writeFile.close();
		saveGraph(graph, "LPA.net");
		
	}
	
	public static void mainAER(String[] args) throws IOException {// SaveQlk
		graph = getFirstComponent(35000);
		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());
		int k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		int[] degrees=getNodesDegrees(graph, k_max);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}

		// удаляю одиночные вершины
	 int c1=0,c2=0;
		List remV= new ArrayList();
		for (Iterator iterator = graph.getVertices().iterator(); iterator.hasNext();) {
			Object v =  iterator.next();
			if(graph.degree(v)==0){c1++;
			remV.add(v);}
			
		}
		for (Object e : graph.getEdges()) {
			Pair p =graph.getEndpoints(e);
			if((graph.degree(p.getFirst())==1)&&
					(graph.degree(p.getSecond())==1)){
				remV.add(p.getFirst());
				remV.add(p.getSecond());
				c2++;
			}
		}
		System.out.println("c1="+c1+"c2="+c2);
		for (Object v : remV) {
			graph.removeVertex(v);
		}
		System.out.println("V:" + graph.getVertexCount());
		System.out.println("E:" + graph.getEdgeCount());

		k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		degrees=getNodesDegrees(graph, k_max);
		for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}
		
		
		
		
		
		//if(2==1+1) return;
		/*k_max = getMaxDegree(graph);
		System.out.println("max_k=" + k_max);
		degrees=getNodesDegrees(graph, k_max+1);*/
		int[][] mass = getQMatrix(graph, k_max);
		File logFile = new File("my_out2.txt");
		FileWriter writeFile = new FileWriter(logFile);
		int count = 0;
		for (int i = 1; i < mass.length; i++) {
			for (int j = 1; j < mass.length; j++) {
				/*
				 * System.out.print(String.format("%.8f", mass[i][j]/2. /
				 * (double) graph.getEdgeCount()) + "\t");
				 */ writeFile.write(String.format("%.8f", mass[i][j] / 2. / (double) graph.getEdgeCount()) + " ");
				// writeFile.write(mass[i][j]+ " ");
				// count=count+mass[i][j];
				/*
				 * System.out.print(String.format("%.2f", mass[i][j]/2.) +
				 * "\t");
				 */
			}
			writeFile.write("\n");
			
			// System.out.println();
		}
		writeFile.close();
		saveGraph(graph, "AER.net");
		/*for (int i = 0; i < degrees.length; i++) {
			System.out.println(degrees[i]);

		}*/
	}

	private static Graph<Integer,Integer> getFirstComponent(int N1) {
		Graph<Integer,Integer> graph = new UndirectedSparseGraph();
		Integer[] mass=new Integer[N1];
		int v_num=0, e_num=0;
		for (int i = 0; i < mass.length; i++) {
			mass[i]= new Integer(v_num++);
			graph.addVertex(mass[i]);
		}
		for (int i = 0; i < mass.length-1; i++) {
			int flag=0;
			for (int j = i+1; j < mass.length; j++) {
				double p_a=2.75/(N1-1);
				double p=(p_a+flag)/2.;
				if(Math.random()<p){
					flag=1;
					graph.addEdge(new Integer(e_num++), mass[i],mass[j]); // создаю ребро между i-м j-м узлом
				} else flag=0;
			}
		}
		return graph;
	}
	
	private static Graph<Integer,Integer> getSecondComponent(int N1) {
		Graph<Integer,Integer> graph = new UndirectedSparseGraph();
	
		return graph;
	}

	static <V> int getMaxDegree(Graph<V, ?> graph) {
		Iterator<V> it = graph.getVertices().iterator();
		int res = 0;
		while (it.hasNext()) {
			V node = it.next();
			int n = graph.degree(node);
			if (res < n)
				res = n;
		}
		return res;
	}

	public static int[][] getQMatrix(Graph<Object, Object> graph, int size) {
		int[][] ret = new int[size][size];
		Collection<Object> list = graph.getEdges();
		for (Object edge : list) {
			Pair<Object> p = graph.getEndpoints(edge);
			Object n1 = p.getFirst();
			Object n2 = p.getSecond();
			/*
			 * if(graph.degree(n1)>graph.degree(n2)) {Object n3=n1; n1=n2;
			 * n2=n3;}
			 */
			int degree_n1 = graph.degree(n1);
			int degree_n2 = graph.degree(n2);
			if (degree_n1 < size && degree_n2 < size) {
				ret[degree_n1][degree_n2] = ret[degree_n1][degree_n2] + 1;
				ret[degree_n2][degree_n1] = ret[degree_n2][degree_n1] + 1;

			}
		}
		return ret;
	}
	private static void saveGraph(Graph g, String filename) {
		PajekNetWriter<Integer, Integer> gm = new PajekNetWriter<Integer, Integer>();
		Transformer<Integer, String> vs = new Transformer<Integer, String>() {

			@Override
			public String transform(Integer arg0) {
				// TODO Auto-generated method stub
				return arg0.toString();
			}

		};
		Transformer<Integer, Number> nev = new Transformer<Integer, Number>() {

			@Override
			public Number transform(Integer arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

		};

		try {
			gm.save(g, new FileWriter(filename), vs, nev);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static Graph<Integer, Integer> loadFromEdgeList(String str) throws IOException {
		Graph gr = new UndirectedSparseGraph();

		BufferedReader br2 = new BufferedReader(new FileReader(str));
		try {

			String sCurrentLine;
			br2 = new BufferedReader(new FileReader(str));
			//System.out.println(str);
			int i = 1;
			while ((sCurrentLine = br2.readLine()) != null) {
				String[] strMass = sCurrentLine.split("\t");
				if (strMass.length == 2) {
					gr.addEdge(new Integer(i++), new Integer(strMass[0]),
							new Integer(strMass[1]));
					// bw.write(strMass[0] + " " + strMass[1]);
					// bw.newLine();

				}

				else
					gr.addVertex(new Integer(strMass[0]));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br2 != null)
					br2.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		//System.out.println(gr);

		return gr;
	}
	public static int[] getTriAndVilk2(Graph<Integer, Integer> graph) {
		// перебираем все вершины
		int count = 0;
		int count2 = 0;
		Collection<Integer> list = graph.getVertices();

		for (Integer node : list) {

			int k = 0;
			for (Integer link : graph.getIncidentEdges(node)) {
				// if (!graph.getOpposite(node, link).isMark())
				k++;
			}
			count2 = count2 + k * (k - 1) / 2;

			Collection<Integer> neig_s = graph.getNeighbors(node);
			Iterator<Integer> it1 = neig_s.iterator();

			// if (!node.isMark())
			while (it1.hasNext()) {
				Integer node1 = it1.next();
				Iterator<Integer> it2 = neig_s.iterator();
				// if (!node1.isMark())
				while (it2.hasNext()) {
					Integer node2 = it2.next();
					if (
					// (!node2.isMark()) &&
					(node1 != node2) && graph.isNeighbor(node1, node2))
						count++;
				}
			}
		}
		return new int[] { count / 6, count2 };
	}

}
