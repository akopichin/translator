import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import net.sf.json.*;
//import java.text.*;

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
//import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
//import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URL;


public class translator {
	public static TrayIcon trayIcon;
	public static JFrame noticeMessage;

	
	public static void main(String[] args) throws Exception {
		//System.out.print("OK");
		PopupMenu popup = new PopupMenu();
		
		MenuItem exitItem = new MenuItem("close");
	    
		exitItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	    
	    popup.add(exitItem);

	    SystemTray systemTray = SystemTray.getSystemTray();

	    Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

	    trayIcon = new TrayIcon(image, "hi there", popup);	    
	    trayIcon.setImageAutoSize(true);
	    
		MouseListener clickClose = new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				
				MouseListener ml = new MouseAdapter() {
					public void mouseClicked(MouseEvent event) {
						closeMsg(noticeMessage);
					}
				};			
				//Icon warnIcon = MetalIconFactory.getTreeComputerIcon();
				String translation = makeTranslation();
				//trayIcon.setToolTip(makeTranslation());
				
				noticeMessage = new JFrame("ok!");
				noticeMessage.setDefaultCloseOperation(0);
				setTranslucency(noticeMessage);
				noticeMessage.setUndecorated(true);
				noticeMessage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				noticeMessage.setBackground(new Color(0f, 0f, 0f, 1f / 3f));
				noticeMessage.pack();

				noticeMessage.addMouseListener(ml);			
				
			    JLabel msg = new JLabel();
			    msg.setText("<html>" + translation + "</html>");
			    msg.setHorizontalTextPosition(JLabel.LEFT);
			    msg.setVerticalTextPosition(JLabel.CENTER);
			    noticeMessage.add(msg);		
			    noticeMessage.setDefaultCloseOperation(1);				
			    
				noticeMessage.setSize(500, 100);	
				noticeMessage.setVisible(true);
			}
		};	    
	    trayIcon.addMouseListener(clickClose);

	    systemTray.add(trayIcon);//*/
     
	}

    private static void setTranslucency( Window window){
        try {
               Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
               Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
               mSetWindowOpacity.invoke(null, window, Float.valueOf(0.75f));
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
    
	/* translate */
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
	    	  //System.out.println( (String)contents.getTransferData(DataFlavor.stringFlavor));
	    	  
	  		  String sourceString = URLEncoder.encode((String)contents.getTransferData(DataFlavor.stringFlavor));
			  String fromLang = "en";
			  String toLang = "ru";
			
				  JSON translate;
				  JSONArray translate_data;
			  URL googleURL = new URL("http://translate.google.com/translate_a/t?tl=" + toLang + "&client=t&hl=en&sl=" + fromLang + "&text=" + sourceString + "&multires=1");
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
	        System.out.println(ex);
	        ex.printStackTrace();
	      }
	      catch (IOException ex) {
	        System.out.println(ex);
	        ex.printStackTrace();
	      }
	    }	
		
		return "error";
	}
	
}