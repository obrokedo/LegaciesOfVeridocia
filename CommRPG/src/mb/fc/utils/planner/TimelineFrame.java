package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultHeaderRenderer;

public class TimelineFrame extends JFrame 
{
	 public static final List _headerList = new ArrayList();

	    public static void main(String[] args) {
	        TimelineFrame f = new TimelineFrame();
	        f.setupUI();

	        // model will be changed by the main thread
	        // startChanging(model);

	    }
	    
	    private void setupUI()
	    {
			setSize(800, 600);
			getContentPane().setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
			TimeBarModel model = createRandomModel(1, 120, 2);
			TimeBarViewer tbv = new TimeBarViewer(model);
			tbv.setDrawOverlapping(false);
			tbv.setRowHeight(100);
			tbv.setHeaderRenderer(new ZHeaderRenderer());
	
			getContentPane().add(tbv, BorderLayout.CENTER);
	
			setVisible(true);
	    }

	    /**
	     * @param model
	     */
	    private static void startChanging(TimeBarModel model) {
	        long delay = 800;
	        for (int r = 0; r < model.getRowCount(); r++) {
	            TimeBarRow row = model.getRow(r);
	            double sum = getIntervalSum(row);
	            DefaultRowHeader header = (DefaultRowHeader) _headerList.get(r);
	            header.setLabel("R" + r + "(" + sum + ")");
	            System.out.println("Changed header " + r);
	            try {
	                Thread.sleep(delay);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        for (int r = 0; r < model.getRowCount(); r++) {
	            TimeBarRow row = model.getRow(r);
	            Iterator it = row.getIntervals().iterator();
	            while (it.hasNext()) {
	                Interval interval = (Interval) it.next();
	                double minutes = interval.getEnd().diffMinutes(interval.getBegin());
	                JaretDate date = interval.getEnd().copy();
	                date.backMinutes(minutes / 4);
	                interval.setEnd(date);
	                double sum = getIntervalSum(row);
	                DefaultRowHeader header = (DefaultRowHeader) _headerList.get(r);
	                header.setLabel("R" + r + "(" + sum + ")");
	                System.out.println("Changed interval " + interval);
	                try {
	                    Thread.sleep(delay / 2);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }

	    private static double getIntervalSum(TimeBarRow row) {
	        double result = 0;
	        Iterator it = row.getIntervals().iterator();
	        while (it.hasNext()) {
	            Interval interval = (Interval) it.next();
	            result += interval.getEnd().diffMinutes(interval.getBegin());
	        }

	        return result;
	    }

	    public TimeBarModel createRandomModel(int rows, int averageLengthInMinutes, int countPerRow) {
	        DefaultTimeBarModel model = new DefaultTimeBarModel();

	        for (int row = 0; row < rows; row++) {
	            DefaultRowHeader header = new DefaultRowHeader("r is really the best label you could imagine" + row);	            
	            _headerList.add(header);
	            DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
	            JaretDate date = new JaretDate();
	            for (int i = 0; i < countPerRow; i++) 
	            {
	            	for (int j = 0; j < 3; j++)
	            	{
	            		JaretDate date2 = date.copy();
		                IntervalImpl interval = new ZIntervalImpl("Poo");
		                int length = averageLengthInMinutes / 2 + (int) (Math.random() * (double) averageLengthInMinutes);
		                interval.setBegin(date2.copy());
		                date2.advanceMinutes(length);
		                interval.setEnd(date2.copy());
		                System.out.println("CREATE");
		                tbr.addInterval(interval);
	            	}

	                int pause = (int) (Math.random() * (double) averageLengthInMinutes);
	                date.advanceMinutes(pause);
	            }
	            model.addRow(tbr);
	        }

	        System.out.println("Created " + (rows * countPerRow) + " Intervals");

	        return model;
	    }
	    
	    public class ZHeaderRenderer extends DefaultHeaderRenderer
	    {
			@Override
			public int getWidth() {
				return 200;
			}	    				
	    }
	    
	    public class ZIntervalImpl extends IntervalImpl
	    {
	    	public String title;
	    	
	    	public ZIntervalImpl(String title)
	    	{
	    		this.title = title;
	    	}

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return title;
			}	    	
	    }
}
