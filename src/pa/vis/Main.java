package pa.vis;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.io.PajekNetWriter;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;
import pa.generators.GenPA;
import pa.generators.PrefferentialAttachment;

public class Main {
	static Factory<Object> vertexFactory = new Factory<Object>() {
		int i=0;
		public Object create() {
			return new Integer(i++);
		}
	};
	static Factory<Object> edgeFactory = new Factory<Object>() {
		public Object create() {
			return new Object();
		}
	};

	public static void mainQMatrix(String[] args) {
		PrefferentialAttachment pa = new PrefferentialAttachment(){

			@Override
			public double f(int k) {
				// (x^2+10)(x+1)
				return ((k*k)+10.)/((double)k+1.);
			}

			
		};
	}	
	public static void writeNodesDegrees(Graph<Object, Object> graph, int length) {
		System.out.println("выводим степени связности");

		Iterator<Object> it = graph.getVertices().iterator();
		long c1 = 1;

		int[] distr0 = new int[length];
		int[] distr1 = new int[length];
		while (it.hasNext()) {
			Object node = it.next();

		
				int mass[] = { graph.degree(node), 0 };
				if (mass[0] < length)
					distr0[mass[0]] = distr0[mass[0]] + 1;
				if (mass[1] < length)
					distr1[mass[1]] = distr1[mass[1]] + 1;
		
		}
		for (int i = 0; i < distr0.length; i++)
			System.out.println(distr0[i]/(double)graph.getVertexCount());
	}
	public static int[][] getQMatrix(Graph<Object,Object> graph, int size) {
        int[][] ret = new int[size][size]; 
        Collection<Object> list =graph.getEdges();
        for (Object edge : list) {
        	Object n1=  graph.getSource(edge);
        	Object n2= graph.getDest(edge);
        /*    if(graph.degree(n1)>graph.degree(n2))
            		{Object n3=n1;
            			n1=n2;
            			n2=n3;}*/
            int degree_n1=graph.degree(n1);
            int degree_n2=graph.degree(n2);
            if(degree_n1<size&&degree_n2<size)
            {
            	ret[degree_n1][degree_n2]=ret[degree_n1][degree_n2]+1;
            }
        }
    return ret;
	}
	public static void main(String[] args){
		PrefferentialAttachment pa = new PrefferentialAttachment(){

			@Override
			public double f(int k) {
				// TODO Auto-generated method stub
				return Math.pow(k,0.75);
			}
			
		};
		double[] r=new double[]{0.0000000, 0.5,0.5};
		GenPA gen = new GenPA(vertexFactory, edgeFactory, r, pa);
		Graph gr = gen.evolve(6, seed_graph());
		frame(gr);

		
	}	

	static  Graph seed_graph() {
	Graph gr = new DirectedSparseMultigraph<Integer, Integer>();
	for (int i = -1; i > -8;i--) {
		Integer n = new Integer(i);
		gr.addVertex(n);
	}
	int l = -1;
	Object[] mass = gr.getVertices().toArray();
	for (int i = 0; i < mass.length - 1; i++)
		for (int j = i + 1; j < mass.length; j++)
			if (i != j)
				gr.addEdge(new Integer(l--), (Integer) mass[i],
						(Integer) mass[j]);

	return gr;
}
	
	private static Graph createRing() {
		Graph gr = new UndirectedSparseMultigraph<Integer, Integer>();
		for (int i = 1; i <= 8; i++) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}

		gr.addEdge(1, 1, 2, EdgeType.UNDIRECTED);
		gr.addEdge(2, 1, 3, EdgeType.UNDIRECTED);
		gr.addEdge(3, 2, 4, EdgeType.UNDIRECTED);
		gr.addEdge(4, 3, 4, EdgeType.UNDIRECTED);
		gr.addEdge(5, 4, 5, EdgeType.UNDIRECTED);
		gr.addEdge(6, 3, 6, EdgeType.UNDIRECTED);
		gr.addEdge(7, 5, 6, EdgeType.UNDIRECTED);
		gr.addEdge(8, 7, 5, EdgeType.UNDIRECTED);
		gr.addEdge(9, 8, 6, EdgeType.UNDIRECTED);
		gr.addEdge(10, 7, 8, EdgeType.UNDIRECTED);
		gr.addEdge(11, 2, 7, EdgeType.UNDIRECTED);
		gr.addEdge(12, 1, 8, EdgeType.UNDIRECTED);

		return gr;

	}

	private static Graph createShram() {
		Graph gr = new UndirectedSparseMultigraph<Integer, Integer>();
		for (int i = 1; i <= 12; i++) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}

		gr.addEdge(1, 1, 9, EdgeType.UNDIRECTED);
		gr.addEdge(2, 9, 5, EdgeType.UNDIRECTED);
		gr.addEdge(3, 9, 10, EdgeType.UNDIRECTED);
		gr.addEdge(4, 2, 10, EdgeType.UNDIRECTED);
		gr.addEdge(5, 10, 6, EdgeType.UNDIRECTED);
		gr.addEdge(6, 10, 11, EdgeType.UNDIRECTED);
		gr.addEdge(7, 3, 11, EdgeType.UNDIRECTED);
		gr.addEdge(8, 11, 7, EdgeType.UNDIRECTED);
		gr.addEdge(9, 11, 12, EdgeType.UNDIRECTED);
		gr.addEdge(10, 4, 12, EdgeType.UNDIRECTED);
		gr.addEdge(11, 12, 8, EdgeType.UNDIRECTED);
		return gr;
	}

	private static Graph createSimple() {

		Graph gr = new UndirectedSparseGraph<Integer, Integer>();
		/*for (int i = -1; i > -21; i--) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}
		int l = -1;
		Object[] mass = gr.getVertices().toArray();
		for (int i = 0; i < mass.length - 1; i++)
			for (int j = i + 1; j < mass.length; j++)
				if (i != j)
					gr.addEdge(new Integer(l--), (Integer) mass[i],
							(Integer) mass[j], EdgeType.UNDIRECTED);*/
		
/*	
		for (int i = 1; i <=13; i++) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}
		gr.addEdge(0,1,2);

		gr.addEdge(1,1,4);
		gr.addEdge(2,2,4);
		gr.addEdge(3,3,4);
		
		gr.addEdge(4,5,8);
		gr.addEdge(5,6,8);
		gr.addEdge(6,7,8);

		gr.addEdge(7,9,12);
		gr.addEdge(8,10,12);
		gr.addEdge(9,11,12);
		
		gr.addEdge(10,4,13);
		gr.addEdge(11,8,13);
		gr.addEdge(12,12,13);
*/
		for (int i = 1; i <=8; i++) {
			Integer n = new Integer(i);
			gr.addVertex(n);
		}
		gr.addEdge(1,1,4);
		gr.addEdge(2,4,3);
		gr.addEdge(3,3,2);
		gr.addEdge(4,2,1);

		gr.addEdge(6,3,7);
		gr.addEdge(7,7,8);
		gr.addEdge(8,8,2);
		
		gr.addEdge(10,8,5);
		gr.addEdge(11,5,6);
		gr.addEdge(12,6,7);

		gr.addEdge(13,4,6);
		gr.addEdge(15,1,5);
		gr.addEdge(16,5,2);
		gr.addEdge(16,5,2);

		gr.addEdge(16,5,2);
		gr.addEdge(16,5,2);
		gr.addEdge(16,5,2);



		return gr;
	}

	// ========================================================================
	static Class[] layoutClasses = new Class[] { CircleLayout.class,
			SpringLayout.class, FRLayout.class, KKLayout.class };
	Class subLayoutType = CircleLayout.class;
	static AggregateLayout<Object, Object> clusteringLayout;
	static DefaultModalGraphMouse graphMouse;

	// ========================================================================
	private static Layout getLayoutFor(Class layoutClass, Graph graph)
			throws Exception {
		Object[] args = new Object[] { graph };
		Constructor constructor = layoutClass
				.getConstructor(new Class[] { Graph.class });
		return (Layout) constructor.newInstance(args);
	}

	private static void heightConstrain(Component component) {
		Dimension d = new Dimension(component.getMaximumSize().width,
				component.getMinimumSize().height);
		component.setMaximumSize(d);
	}

	 static void frame(final Graph g) {
		JFrame frame = new JFrame("Увеличенное количество обратный");

		Layout<Object, Object> layout = new SpringLayout(g);
		layout.setSize(new Dimension(300, 300)); // sets the initial size of the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		final VisualizationViewer<Object, Object> vv;// = new
														// VisualizationViewer<Object,
														// Object>( layout);
		// vv.setPreferredSize(new Dimension(350, 350));

		clusteringLayout = new AggregateLayout<Object, Object>(
				new FRLayout<Object, Object>(g));
		final VisualizationModel<Object, Object> visualizationModel = new DefaultVisualizationModel<Object, Object>(clusteringLayout);
		vv = new VisualizationViewer<Object, Object>(visualizationModel);
		vv.setBackground(Color.white);
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	// vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		 vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		
		graphMouse = new DefaultModalGraphMouse();
		vv.setGraphMouse(graphMouse);
		final PredicatedParallelEdgeIndexFunction eif = PredicatedParallelEdgeIndexFunction
				.getInstance();
		final Set exclusions = new HashSet();
		Transformer pTrans = vv.getRenderContext()
				.getVertexFillPaintTransformer();
		vv.getRenderContext().setVertexFillPaintTransformer(
				new Transformer<Object, Paint>() {
					public Color transform(Object arg0) {
						
						return (((Integer)arg0)>=0)? Color.green:Color.red;
					}
				});

		GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		final ScalingControl scaler = new CrossoverScalingControl();
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});
		JComboBox layoutTypeComboBox = new JComboBox(layoutClasses);
		layoutTypeComboBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString
						.lastIndexOf('.') + 1);
				return super.getListCellRendererComponent(list, valueString,
						index, isSelected, cellHasFocus);
			}
		});
		layoutTypeComboBox.setSelectedItem(SpringLayout.class);
		layoutTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Class clazz = (Class) e.getItem();
					try {
						Layout<Object, Object> layout = getLayoutFor(clazz, g);
						layout.setInitializer(vv.getGraphLayout());
						clusteringLayout.setDelegate(layout);
						vv.setGraphLayout(clusteringLayout);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		Box save = Box.createVerticalBox();
		JButton buttonSave = new JButton("  Save  graph  ");
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PajekNetWriter<Object, Integer> gm = new PajekNetWriter<Object, Integer>();

				JFileChooser chooser = new JFileChooser();
				// chooser.setFileFilter(new NetGrhFilter());
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setSelectedFile(new File("graph.net"));

				try {
					chooser.setCurrentDirectory(new File(new File("./graphs")
							.getCanonicalPath()));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				int returnVal = chooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					try {
						File selectedFile = chooser.getSelectedFile();
						gm.save(g,
								new FileWriter(selectedFile.getAbsolutePath()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});
		save.add(buttonSave);
		JButton btn2 = new JButton("Statistic prop.");
		btn2.addActionListener(new ActionListener() {
			private void heightConstrain2(Component component) {
				Dimension d = new Dimension(component.getMaximumSize().width,
						component.getMinimumSize().height);
				component.setMaximumSize(d);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * final JFrame frame = new JFrame(
				 * "Generated graph degree distribution"); JPanel chartPanel =
				 * createChartPanel();
				 * 
				 * // frame.add(controls, BorderLayout.EAST);
				 * 
				 * frame.setResizable(false); frame.add(chartPanel);
				 * frame.setVisible(true); frame.pack();
				 */
			}
		});
		save.add(Box.createVerticalStrut(5));
		save.add(btn2);
		Dimension space = new Dimension(20, 20);
		Box controls = Box.createVerticalBox();
		// controls.add(Box.createRigidArea(space));
		JPanel zoomControls = new JPanel(new GridLayout(1, 2));
		zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
		zoomControls.add(plus);
		zoomControls.add(minus);
		heightConstrain(zoomControls);
		controls.add(zoomControls);
		controls.add(Box.createRigidArea(space));
		JPanel layoutControls = new JPanel(new GridLayout(0, 1));
		layoutControls.setBorder(BorderFactory.createTitledBorder("Layout"));
		layoutControls.add(layoutTypeComboBox);
		heightConstrain(layoutControls);
		controls.add(layoutControls);
		JPanel modePanel = new JPanel(new GridLayout(1, 1));
		modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		modePanel.add(modeBox);
		heightConstrain(modePanel);
		
		
		controls.add(modePanel);
		// controls.add(Box.createRigidArea(space));
		controls.add(save);
		JPanel pan = new JPanel(new BorderLayout());
		pan.add(controls, BorderLayout.EAST);
		pan.add(gzsp, BorderLayout.CENTER);
		JScrollPane sp = new JScrollPane(pan);
		// frame.add(sp);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(sp);

		/*
		 * DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		 * gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); vv.setGraphMouse(gm);
		 */

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	// загрузка графа
	public static Graph getNet(String fileName) {
		Factory<Object> vertexFactory = new Factory<Object>() {
			int count =1;
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
			pnr = new PajekNetReader<Graph<Object, Object>, Object, Object>(
					vertexFactory, edgeFactory);
			File file = new File(fileName);
			pnr.load(fileName, graph);

		} catch (IOException e5) {
			System.out.println("IOException!!!!!!!!!!!!!!!!!!");
		}
		// System.out.println("Nodes num=" + graph.getVertexCount());
		// System.out.println("Edges num=" + graph.getEdgeCount());
		return graph;
	}
}
