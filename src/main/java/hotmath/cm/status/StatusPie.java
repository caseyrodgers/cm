package hotmath.cm.status;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.testset.ha.HaTest;

import java.awt.Color;
import java.io.File;
import java.text.AttributedString;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

/** Creates a status pie chart used in CM to identify each
 *  possible state in a Program and Prescription.
 *  
 *  Each pie shows three types of slices: current, past, left.  Each type has a specific
 *  color.
 *
 *  static images are created by running the this class's main. 
 *  
 *  
 * @author casey
 *
 */
public class StatusPie {
    File imageFile;
    String baseDir;

    public StatusPie(String baseDir) throws Exception {
        this.baseDir = baseDir;
    }


    /**
     * Create chart that shows the percentage of completion for this
     * program/user.
     * 
     * Color-1=Current section; Color-2=completed sections; Color 3=Sections
     * remaining
     * 
     * @throws Exception
     */
    public void createProgramStatusChart(HaTest haTest) throws Exception {
        int segmentCount = haTest.getTotalSegments();
        int currentSegment = haTest.getSegment();
        
        createStatusChart(segmentCount, currentSegment);
    }

    /**
     * Create chart that shows the percentage of completion through this
     * prescription
     * 
     * @throws Exception
     */
    public void createPrescriptionStatusChart(AssessmentPrescription prescription) throws Exception {
        List<AssessmentPrescriptionSession> sessions = prescription.getSessions();

        int totalSessions = sessions.size();
        int currentSession = prescription.getTest().getUser().getActiveTestRunSession();
        
        createStatusChart(totalSessions, currentSession);
    }    

    public File getImageFile() {
        return imageFile;
    }
    
    
    /** Create the chart with total slices, marking the current one
     * specified as 'current'.
     * 
     * Only create if not already present.
     * 
     * @param total
     * @param current
     * @throws Exception
     */
    public void createStatusChart(int total, int current) throws Exception {
        try {
            imageFile = new File(baseDir, "status-" + total + "_" + current + ".png");
            
            /**
             * Create and populate a PieDataSet with a slice for each piece
             * 
             */
            DefaultPieDataset data = new DefaultPieDataset();
            for (int i = 0; i < total; i++) {
                data.setValue("Seg: " + i, new Long(1));
            }

            PiePlot plot = new PiePlot();
            plot.setDataset(data);
            plot.setBackgroundPaint(new Color(67, 70, 75));  
            plot.setInsets(new RectangleInsets(-1,-1,-1,-1));
            plot.setCircular(true);

            /**
             * Color-1=Current section; Color-2=completed sections; Color
             * 3=Sections remaining
             */
            Color[] colors = new Color[total];
            for (int i = 0; i < total; i++) {
                if ((i + 1) == current) {
                    colors[i] = Color.blue;
                } else if ((i + 1) < current) {
                    colors[i] = Color.GREEN;
                } else {
                    colors[i] = Color.YELLOW;
                }
            }
            PieRenderer renderer = new PieRenderer(colors);
            renderer.setColor(plot, data);

            /** Turn off labels
             * 
             */
            plot.setLabelGenerator(new CustomLabelGenerator());
            
            JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
            chart.setBorderVisible(false);
            // Write the chart image to the temporary directory
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            // filename = ServletUtilities.saveChartAsPNG(chart, 500, 300, info,
            // session);
            // Write the image map to the PrintWriter
            
            ChartUtilities.saveChartAsPNG(imageFile, chart, 150, 150, info);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }    

    /**
     * Disable labels
     */
    static class CustomLabelGenerator implements PieSectionLabelGenerator {
        /**
         * return null to disable labels
         * 
         */
        public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
            return null;
        }

        @Override
        public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
            return null;
        }
    }

    static class PieRenderer {
        /* Declaring an array of Color variables for storing a list of Colors */
        private Color[] color;

        /* Constructor to initialize PieRenderer class */
        public PieRenderer(Color[] color) {
            this.color = color;
        }

        /**
         * Set Method to set colors for pie sections based on our choice
         * 
         * @param plot
         *            PiePlot of PieChart
         * @param dataset
         *            PieChart DataSet
         */
        public void setColor(PiePlot plot, DefaultPieDataset dataset) {
            List keys = dataset.getKeys();
            int aInt;

            for (int i = 0; i < keys.size(); i++) {
                aInt = i % this.color.length;
                plot.setSectionPaint(keys.get(i).toString(), this.color[aInt]);
            }
        }
    }
    
    
    static public void main(String as[])  {
        try {
            File base = new File("/dev/local/gwt2/cm/src/main/webapp/gwt-resources/images/status");
            if(!base.exists())
                base.mkdirs();
    
            StatusPie statusPie = new StatusPie(base.getCanonicalPath());
            
            int MAX_SIZES=25;  /** total possible number of sessions */
            /** create a status for each permutation
             * 
             */
            for(int size=1;size<MAX_SIZES;size++) {
                for(int slice=0;slice < size;slice++) {
                    statusPie.createStatusChart(size,slice+1);
                }
            }
            System.out.println("Created all status charts");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
