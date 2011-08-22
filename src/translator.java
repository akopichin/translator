//import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import net.sf.json.*;

import javax.swing.*;
//import javax.swing.plaf.metal.MetalIconFactory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URL;

/**
 * 
 * This class serve to translate text from clipboard
 * to russian with google.translate
 * 
 * @author akopichin
 *
 */
public class translator {
	public static TrayIcon trayIcon;
	public static JFrame noticeMessage;
	public static String sourceLang = "en";
	public static String translationLang = "ru";
	
	/**
	 * All magic is here = )
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//create popup menu 
		PopupMenu popup = new PopupMenu();
		
		MenuItem exitItem = new MenuItem("close");
	    
		exitItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	    
	    popup.add(exitItem);
	    
	    //create tray icon
	    SystemTray systemTray = SystemTray.getSystemTray();

	    Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

	    trayIcon = new TrayIcon(image, "hi there", popup);	    
	    trayIcon.setImageAutoSize(true);

	    class RemindTask extends TimerTask {
	    	public void run() {
	    		closeMsg(noticeMessage);
	    	}
	    }	    
	    
		MouseListener clickClose = new MouseAdapter() {
			/**
			 * then clicked on tray icon
			 * create floating notice 
			 * and 
			 */
			public void mouseClicked(MouseEvent event) {
				
			    Timer timer = new Timer();
				
				MouseListener ml = new MouseAdapter() {
					public void mouseClicked(MouseEvent event) {
						closeMsg(noticeMessage);
					}
				};

				String translation = makeTranslation();
				
				if (noticeMessage != null) {
					closeMsg(noticeMessage);
				}
				noticeMessage = new JFrame();
 				noticeMessage.setDefaultCloseOperation(0);
				setTranslucency(noticeMessage);
				noticeMessage.setUndecorated(true);
				noticeMessage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				noticeMessage.setBackground(new Color(0f, 0f, 0f, 1f / 3f));

				noticeMessage.addMouseListener(ml);			
				
			    JLabel msg = new JLabel();
			    msg.setText("<html>" + translation + "</html>");
			    msg.setHorizontalTextPosition(JLabel.LEFT);
			    msg.setVerticalTextPosition(JLabel.CENTER);
			    noticeMessage.add(msg);		
			    noticeMessage.setDefaultCloseOperation(1);				
			    
			    noticeMessage.pack();	
				noticeMessage.setVisible(true);
				
				timer.schedule(new RemindTask(), 15 * 1000);				
			}
			
		};	    
	    trayIcon.addMouseListener(clickClose);

	    systemTray.add(trayIcon);//*/
	}

	
    private static void setTranslucency(Window window){
        try {
               Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
               Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
               mSetWindowOpacity.invoke(null, window, Float.valueOf(0.6f));
            } catch (NoSuchMethodException ex) {
               ex.printStackTrace();
            } catch (SecurityException ex) {
               ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
            } catch (IllegalAccessException ex) {
               ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
               ex.printStackTrace();
            } catch (InvocationTargetException ex) {
               ex.printStackTrace();
            }
    }

    private static void closeMsg(JFrame j) {    	
        j.dispose();
	}    
    
	/**
	 * translatin' here
	 * 
	 * @return String
	 */
	static String makeTranslation() {
		String simpleTranslate = "";
		String translateDetails = "";
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText =
		      (contents != null) &&
		      contents.isDataFlavorSupported(DataFlavor.stringFlavor)
		    ;
		if ( hasTransferableText ) {
	      try {
	  		  String sourceString = URLEncoder.encode((String)contents.getTransferData(DataFlavor.stringFlavor), "UTF-8");
			
				  JSON translate;
				  JSONArray translate_data;
			  URL googleURL = new URL("http://translate.google.com/translate_a/t?tl=" + sourceLang + "&client=t&hl=en&sl=" + translationLang + "&text=" + sourceString + "&multires=1");
  			  URLConnection googleConnection = googleURL.openConnection();
			  googleConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

			  BufferedReader in = new BufferedReader(new InputStreamReader(
			  		googleConnection.getInputStream()));
			  String inputLine;
			  String json = "";
			
			  while ((inputLine = in.readLine()) != null)
				json += inputLine;
			  in.close();
			
			  translate = JSONSerializer.toJSON(json);
			  translate_data = JSONArray.fromObject(JSONSerializer.toJava(translate));

			  simpleTranslate = translate_data.getJSONArray(0).getString(0).replaceAll("[\\[\"\\]]", "").replace(","," | ");
			  translateDetails = translate_data.getString(1)
				.replaceAll("[\\[\"\\]]", "")
					.replaceAll("(noun,|verb,|adjective,|interjection,)", "\n$1\n<br />")
						.replace(",",", ").replaceAll("(^, )", "");
			
			  
			  return simpleTranslate + "\n<br />" + translateDetails;
	      }
	      catch (UnsupportedFlavorException ex){
	        ex.printStackTrace();
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }	
		
		return "error";
	}
	
}