
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.chart.ScatterChart;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Element;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author enter
 */

public class MainView extends javax.swing.JFrame {
    FileFilter filter = new FileNameExtensionFilter("Archivos Texto o Java ", new String[] {"java", "txt"});
    
    public static Java8Lexer lexer;
    String archivo = null;
    String ruta = null;
    String code = null;
    public static String noused = null;
    public static int numnoclase = 0;
    

    public static boolean exportToCSV(JTable tableToExport, String pathToExportTo) {
        try {
            TableModel model = tableToExport.getModel();
            FileWriter csv = new FileWriter(new File(pathToExportTo));

            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i) + ",");
            }

            csv.write("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    csv.write(model.getValueAt(i, j).toString() + ",");
                }
                csv.write("\n");
            }

            csv.close();
            JOptionPane.showMessageDialog(null, "ARCHIVO CREADO  \n Ruta: "+pathToExportTo);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL CREAR EL ARCHIVO");
            e.printStackTrace();
            return false;
        }
    }
    
    public static void oler(String rutaarchivo){
        String informe = null;
        
        noused = "__CLASES NO USADAS__\n";
        
        int numvar = 0;
        int numfun = 0;
        int numclase = 0;
        
        try {
                lexer = new Java8Lexer(CharStreams.fromFileName(rutaarchivo));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "ERROR al abrir el archivo.", "ERROR AL OLER!", JOptionPane.ERROR_MESSAGE);
            }
        // Identificar al analizador léxico como fuente de tokens para el sintactico
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Crear el objeto correspondiente al analizador sintáctico que se alimenta a partir del buffer de tokens
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); // Iniciar el analisis sintáctico en la regla inicial: r
        System.out.println(tree.toStringTree(parser)); // imprime el arbol al estilo LISP
        MyVisitors<Object> loader = new MyVisitors<Object>();
        loader.visit(tree);
        ArrayList<String> tot= new ArrayList<String>();
        ArrayList<String> fun = new ArrayList<String>();
        ArrayList<function> objfun = new ArrayList<function>();
        ArrayList<String> vf = new ArrayList<String>();
        System.out.println("    ");
        loader.tablaVariables.forEach((k,v) -> {
            vf.add(k);
            tot.add(k);
        });
        loader.tablaFunciones.forEach((k,v) -> {
            fun.add(k);
            tot.add(k);
            objfun.add(v);
        });
        ArrayList<String> cla = new ArrayList<String>();
        ArrayList<Nclase> objcla = new ArrayList<Nclase>();
        loader.tablaClases.forEach((k,v) -> {
            cla.add(k);
            tot.add(k);
            objcla.add(v);
        });
        loader.tablaClases.forEach((k,v) -> {
            cla.add(k);
            tot.add(k);
            if(v.calls<1){
                noused += "-"+v.name+"\n";
                numnoclase++;
                loader.smells.add(new smell(v.getT(),
                    "La clase no es usada!.\n", "https://refactoring.guru/smells/lazy-class"));
            }
        });
        
        labelnoused.setText(Integer.toString(numnoclase));
        numnoclase=0;

        for (int i = 0; i < loader.smells.size(); i++) {
            AnadirOlor(loader.smells.get(i).row,loader.smells.get(i).col,loader.smells.get(i).description);
        }
        labelolores.setText(Integer.toString(loader.smells.size()));
        
        DefaultPieDataset dataset = new DefaultPieDataset();

        //DATOS
        informe = "♦ VARIABLES \n";
        for (int c = 0; c < vf.size(); c++) {
            informe += "-"+vf.get(c)+"\n";
            numvar=c;
        }
        informe += "_______________________________\n";
        labelvar.setText(Integer.toString(numvar));
        dataset.setValue( "Variable: "+numvar , new Double( numvar ) );
        
        informe += "♠ FUNCIONES \n";
        for (int d = 0; d < fun.size(); d++) {
            informe += "-"+fun.get(d)+"\n";
            numfun=d;
        }
        informe += "_______________________________\n";
        labelfunc.setText(Integer.toString(numfun));
        dataset.setValue( "Funcion: "+numfun , new Double( numfun) );
        
        informe += "♣ CLASES \n";
        for(int e =0;e<cla.size();e++){
            informe += "-"+cla.get(e)+"\n";
            numclase=e;
        }
        informe += "_______________________________\n";
        labelclases.setText(Integer.toString(numclase));
        dataset.setValue( "Clase: "+numclase , new Double( numclase) );
        
        graficainit(dataset,panelgrafica);
        
        
        DefaultCategoryDataset dataset3 = new DefaultCategoryDataset();
        
        for(int c=0;c<tot.size();c++){
            int dl=0;
            for(int d=c+1;d<tot.size();d++){
                dl=computeLevenshteinDistance(tot.get(c),tot.get(d));
                if(dl <= 5){
                    dataset3.addValue(dl,tot.get(d),tot.get(c));
                }
            }
        }
        
        DefaultCategoryDataset dataset1 =  new DefaultCategoryDataset( ); 
        for(int i=0;i<objcla.size();i++){
            dataset1.addValue(objcla.get(i).calls, objcla.get(i).name, "# Usos");
        }
        grafica1_2(dataset1, panelgrafica1, "Llamadas de clases", "Clases");
        
        DefaultCategoryDataset dataset2 =  new DefaultCategoryDataset( ); 
        for(int i=0;i<objfun.size();i++){
            dataset2.addValue(objfun.get(i).calls, objfun.get(i).name, "# Usos");
        }
        grafica1_2(dataset2, panelgrafica2, "Llamadas de funciones", "Funciones");
        
        grafica3(dataset3, panelgrafica3);
        informe+=noused;
        informebox.setText(informe);
    }
    
    
     private static int minimum(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public static int computeLevenshteinDistance(String str1, String str2) {
        return computeLevenshteinDistance(str1.toCharArray(),
                str2.toCharArray());
    }
    private static int computeLevenshteinDistance(char [] str1, char [] str2) {
        int [][]distance = new int[str1.length+1][str2.length+1];

        for(int i=0;i<=str1.length;i++){
            distance[i][0]=i;
        }
        for(int j=0;j<=str2.length;j++){
            distance[0][j]=j;
        }
        for(int i=1;i<=str1.length;i++){
            for(int j=1;j<=str2.length;j++){
                distance[i][j]= minimum(distance[i-1][j]+1,
                        distance[i][j-1]+1,
                        distance[i-1][j-1]+
                                ((str1[i-1]==str2[j-1])?0:1));
            }
        }
        return distance[str1.length][str2.length];

    }
    
    //GRAFICAS
    public static void graficainit(DefaultPieDataset set, JPanel panel) {
        //JFreeChart grafica1 = ChartFactory.createScatterPlot( "Numero de Elementos Identificados", "X-Axis", "Y-Axis", set);
        JFreeChart grafica1 = ChartFactory.createPieChart3D("Numero de Elementos Identificados", set);
        ChartPanel chartPanel = new ChartPanel(grafica1);
        panel.removeAll();
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
    }
    
    public static void grafica1_2(CategoryDataset set, JPanel panel,String titulo,String base) {
        JFreeChart barChart = ChartFactory.createBarChart(titulo, base, "Veces usado", set,PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );       
        panel.removeAll();
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
    }
    
    public static void grafica3(CategoryDataset set, JPanel panel) {
        JFreeChart grafica3 = ChartFactory.createBarChart("Grafica de Similitudes","Elementos","Cant de diferencias",set,PlotOrientation.VERTICAL,true,true,false);
        //JFreeChart grafica1 = ChartFactory.createPieChart3D("Numero de Elementos Identificados", set);
        ChartPanel chartPanel = new ChartPanel(grafica3);
        panel.removeAll();
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
    }

    
    public static void AnadirOlor(int fil,int col, String dataRow)
    {
        DefaultTableModel model = (DefaultTableModel)oloresTab.getModel();
        //model.addRow(rowData);
        model.addRow(new Object[]{fil,col,dataRow});
    }
    public static void reset() {
        
        DefaultTableModel model = (DefaultTableModel) oloresTab.getModel();
        model.setRowCount(0);
    }
  
 

    
    /**
     * Creates new form MainView
     */
    public MainView() {
        initComponents();
        seticon();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jFileChooser2 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelPrincipal = new javax.swing.JPanel();
        PanelVisor = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        codigoBox = new javax.swing.JTextArea();
        panelgrafica = new javax.swing.JPanel();
        PanelOlores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        oloresTab = new javax.swing.JTable();
        ButtonNose = new javax.swing.JButton();
        exportar = new javax.swing.JButton();
        panelInfo = new javax.swing.JPanel();
        panelgrafica1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panelgraf2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        informebox = new javax.swing.JTextArea();
        Resume = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelolores = new javax.swing.JLabel();
        labelclases = new javax.swing.JLabel();
        labelvar = new javax.swing.JLabel();
        labelfunc = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labelnoused = new javax.swing.JLabel();
        panelgrafica2 = new javax.swing.JPanel();
        panelSimil = new javax.swing.JPanel();
        panelgrafica3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem5.setText("jMenuItem5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("YV7Smeller - Detector de Malos Olores");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jFileChooser2.setDialogTitle("Abrir");
        jFileChooser2.setFileHidingEnabled(false);
        getContentPane().add(jFileChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        panelPrincipal.setOpaque(false);

        PanelVisor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Visor de Codigo", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        codigoBox.setEditable(false);
        codigoBox.setColumns(20);
        codigoBox.setRows(15);
        jScrollPane1.setViewportView(codigoBox);

        javax.swing.GroupLayout PanelVisorLayout = new javax.swing.GroupLayout(PanelVisor);
        PanelVisor.setLayout(PanelVisorLayout);
        PanelVisorLayout.setHorizontalGroup(
            PanelVisorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVisorLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1049, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        PanelVisorLayout.setVerticalGroup(
            PanelVisorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVisorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        panelgrafica.setOpaque(false);
        panelgrafica.setLayout(new java.awt.BorderLayout());

        PanelOlores.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Olores", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        PanelOlores.setOpaque(false);

        oloresTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Fila", "Columna", "Descripcion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        oloresTab.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        oloresTab.setOpaque(false);
        oloresTab.setRowMargin(2);
        jScrollPane2.setViewportView(oloresTab);
        if (oloresTab.getColumnModel().getColumnCount() > 0) {
            oloresTab.getColumnModel().getColumn(0).setMinWidth(40);
            oloresTab.getColumnModel().getColumn(0).setPreferredWidth(40);
            oloresTab.getColumnModel().getColumn(0).setMaxWidth(120);
            oloresTab.getColumnModel().getColumn(1).setMinWidth(50);
            oloresTab.getColumnModel().getColumn(1).setPreferredWidth(60);
            oloresTab.getColumnModel().getColumn(1).setMaxWidth(120);
            oloresTab.getColumnModel().getColumn(2).setPreferredWidth(10);
        }

        ButtonNose.setText("Activa la Nariz!");
        ButtonNose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonNoseActionPerformed(evt);
            }
        });

        exportar.setText("Exportar a .CSV");
        exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelOloresLayout = new javax.swing.GroupLayout(PanelOlores);
        PanelOlores.setLayout(PanelOloresLayout);
        PanelOloresLayout.setHorizontalGroup(
            PanelOloresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOloresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(PanelOloresLayout.createSequentialGroup()
                .addGap(124, 124, 124)
                .addComponent(ButtonNose)
                .addGap(27, 27, 27)
                .addComponent(exportar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelOloresLayout.setVerticalGroup(
            PanelOloresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOloresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOloresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonNose)
                    .addComponent(exportar))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelVisor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(PanelOlores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelgrafica, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addComponent(PanelVisor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelgrafica, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelOlores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(390, 390, 390))
        );

        jTabbedPane1.addTab("Vista Principal", panelPrincipal);

        panelInfo.setOpaque(false);

        panelgrafica1.setOpaque(false);
        panelgrafica1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos"));
        jPanel1.setOpaque(false);

        panelgraf2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelgraf2.setLayout(new java.awt.BorderLayout());

        informebox.setEditable(false);
        informebox.setColumns(20);
        informebox.setRows(5);
        jScrollPane3.setViewportView(informebox);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(663, 663, 663)
                    .addComponent(panelgraf2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(184, 184, 184))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(panelgraf2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 275, Short.MAX_VALUE)))
        );

        Resume.setText("Informacion General :");

        jLabel2.setText("Numero de Olores detectados: ");

        jLabel3.setText("Cantidad de Clases detectadas:");

        jLabel4.setText("Cantidad de Funciones detectadas:");

        jLabel5.setText("Cantidad de Variables detectadas:");

        labelolores.setText("0");

        labelclases.setText("0");

        labelvar.setText("0");

        labelfunc.setText("0");

        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("Cantidad de Clases NO Usadas:");

        labelnoused.setForeground(new java.awt.Color(255, 0, 0));
        labelnoused.setText("0");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Resume)
                            .addGroup(panelInfoLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addGap(49, 49, 49)
                                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelnoused)
                                    .addComponent(labelfunc)
                                    .addComponent(labelvar)
                                    .addComponent(labelolores)
                                    .addComponent(labelclases))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelgrafica1, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Resume)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(labelolores))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(labelclases))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelvar)
                            .addComponent(jLabel5)))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(panelgrafica1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelfunc)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(labelnoused))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Información", panelInfo);

        panelgrafica2.setOpaque(false);
        panelgrafica2.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Llamados por Funcion", panelgrafica2);

        panelgrafica3.setOpaque(false);
        panelgrafica3.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panelSimilLayout = new javax.swing.GroupLayout(panelSimil);
        panelSimil.setLayout(panelSimilLayout);
        panelSimilLayout.setHorizontalGroup(
            panelSimilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1148, Short.MAX_VALUE)
            .addGroup(panelSimilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelSimilLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelgrafica3, javax.swing.GroupLayout.PREFERRED_SIZE, 1129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(13, Short.MAX_VALUE)))
        );
        panelSimilLayout.setVerticalGroup(
            panelSimilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
            .addGroup(panelSimilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSimilLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelgrafica3, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(17, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Similitudes", panelSimil);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 1150, 630));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Vista Principal");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fondo.png"))); // NOI18N
        jLabel1.setText(" ");
        jLabel1.setFocusable(false);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(622, 280, 500, 300));

        jMenu1.setText("Archivo");

        jMenuItem6.setText("Abrir");
        jMenuItem6.setOpaque(false);
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem3.setText("Salir");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ayuda");
        jMenu2.setOpaque(false);

        jMenuItem4.setText("Acerca de");
        jMenuItem4.setOpaque(false);
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null, "Programa elaborado como proyecto final para la asignatura de Lenguages de Programacion 2020-1. \n INTEGRANTES: \n -Andres Romero Romero \n -Oscar Andres Mancera Garzón \n -Yerson Andres Valderrama \n \n DOCENTE: \n Felipe Restrepo Calle","Acerca de" , JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        jFileChooser2.setCurrentDirectory(new java.io.File("."));
        jFileChooser2.setDialogTitle("choosertitle");
        jFileChooser2.setFileFilter(filter);
        jFileChooser2.addChoosableFileFilter(filter);


        this.jFileChooser2.setMultiSelectionEnabled(true);
        int returnVal = this.jFileChooser2.showOpenDialog(null);
        //if the user confirms file selection display a message  
        if (returnVal == this.jFileChooser2.APPROVE_OPTION) {
            codigoBox.repaint();
            archivo = jFileChooser2.getSelectedFile().getName();
            ruta= jFileChooser2.getSelectedFile().getAbsolutePath()+".csv";
            JOptionPane.showMessageDialog(null, "Archivo seleccionado. "+archivo);
            archivo = jFileChooser2.getSelectedFile().toString();
            
        } else {
            JOptionPane.showMessageDialog(null, "Ha cancelado la seleccion.");
        }
        
        try {
            code = Files.lines(Paths.get(archivo)).collect(Collectors.joining("\n"));
            codigoBox.setText(code);
            TextLineNumber tln = new TextLineNumber(codigoBox);
            jScrollPane1.setRowHeaderView(tln);
        } catch (IOException ex) {
            System.out.println("Error de paginacion");
        }

    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void ButtonNoseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonNoseActionPerformed

        // TODO add your handling code here:
        Java8Lexer lexer;
        if (codigoBox.getText().length() > 0) {
            reset();
            oler(archivo);
        } else {
            JOptionPane.showMessageDialog(null, "NO hay texto para oler. Seleccione primero un archivo.", "ERROR AL OLER!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ButtonNoseActionPerformed

    private void exportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportarActionPerformed

            // TODO add your handling code here:
            exportToCSV(oloresTab, ruta);
    }//GEN-LAST:event_exportarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainView().setVisible(true);
            }
        });
    }
    private void seticon() {
     setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonNose;
    private javax.swing.JPanel PanelOlores;
    private javax.swing.JPanel PanelVisor;
    private javax.swing.JLabel Resume;
    private javax.swing.JTextArea codigoBox;
    private javax.swing.JButton exportar;
    public static javax.swing.JTextArea informebox;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    public static javax.swing.JLabel labelclases;
    public static javax.swing.JLabel labelfunc;
    public static javax.swing.JLabel labelnoused;
    public static javax.swing.JLabel labelolores;
    public static javax.swing.JLabel labelvar;
    public static javax.swing.JTable oloresTab;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelSimil;
    public static javax.swing.JPanel panelgraf2;
    public static javax.swing.JPanel panelgrafica;
    public static javax.swing.JPanel panelgrafica1;
    public static javax.swing.JPanel panelgrafica2;
    public static javax.swing.JPanel panelgrafica3;
    // End of variables declaration//GEN-END:variables
}
