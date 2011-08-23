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
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URL;

import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;

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
	private static final int MY_HOTKEY_INDEX = 1;
	private static boolean hotkeyEventReceived = false;
	private static Timer timer;
	private static Robot robo;
	private static Image iconEnRu;
	private static Image iconRuEn;
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

	    iconEnRu = Toolkit.getDefaultToolkit().getImage("icon_en-ru.png");
	    iconRuEn = Toolkit.getDefaultToolkit().getImage("icon_ru-en.png");

	    trayIcon = new TrayIcon(iconEnRu, "Translator: select text and press ALT+Z", popup);	    
	    trayIcon.setImageAutoSize(true);
	    
	    
		MouseListener clickClose = new MouseAdapter() {
			
		/**
		 * change direction
		 * of translation 
		 * en-ru <=> ru-en
		 */
		public void mouseClicked(MouseEvent event) {			
			String tmp = sourceLang;
			sourceLang = translationLang;
			translationLang = tmp;
			if (sourceLang == "en"){
				trayIcon.setImage(iconEnRu);
			} else {				
				trayIcon.setImage(iconRuEn);
			}
		}
			
		};	    
	    trayIcon.addMouseListener(clickClose);

	    systemTray.add(trayIcon);
	    
	    // Hotkeys 
	    System.load(new File("lib/libJXGrabKey.so").getCanonicalPath());
	    
	    //JXGrabKey.setDebugOutput(true);
	    
		try{
			int key = KeyEvent.VK_Z, mask = KeyEvent.ALT_MASK;
			
			JXGrabKey.getInstance().registerAwtHotkey(MY_HOTKEY_INDEX, mask, key);
		}catch(HotkeyConflictException e){
			JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			
			JXGrabKey.getInstance().cleanUp(); 
			return;
		}	    
	    
		//Implement HotkeyListener
		HotkeyListener hotkeyListener = new jxgrabkey.HotkeyListener(){
			public void onHotkey(int hotkey_idx) {
				if (hotkey_idx != MY_HOTKEY_INDEX)
					return;
				hotkeyEventReceived = true;

				showTranslation();
				
			}
        };
        
        //Add HotkeyListener
		JXGrabKey.getInstance().addHotkeyListener(hotkeyListener);
		
		//Wait for Hotkey Event
		while(!hotkeyEventReceived){
			Thread.sleep(1000);
		}
	}

    public static class TTask extends TimerTask {
    	public void run() {
    		closeMsg(noticeMessage);
    	}
    }	
	
	private static void showTranslation() {
		try {
			robo = new Robot();
			robo.keyPress(KeyEvent.VK_CONTROL);
			robo.delay(500);
			robo.keyPress(KeyEvent.VK_C);
			robo.keyRelease(KeyEvent.VK_CONTROL);
			robo.keyRelease(KeyEvent.VK_C);
			robo = null;
			
			timer = new Timer();			
			
			if (noticeMessage != null) {
				closeMsg(noticeMessage);
			}
						
			MouseListener ml = new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					closeMsg(noticeMessage);
				}
			};

			String translation = makeTranslation();
			
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
		    
			try {
				timer.schedule(new translator.TTask(), 15 * 1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}catch(AWTException e){
			e.printStackTrace();					
		}		
				
	}
	
    private static void setTranslucency(Window window){
        try {
               Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
               Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
               mSetWindowOpacity.invoke(null, window, Float.valueOf(0.8f));
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
		if (hasTransferableText) {
	      try {
	  		  String sourceString = URLEncoder.encode((String)contents.getTransferData(DataFlavor.stringFlavor), "UTF-8");
			
				  JSON translate;
				  JSONArray translate_data;
			  URL googleURL = new URL("http://translate.google.com/translate_a/t?tl=" + translationLang + "&client=t&hl=en&sl=" + sourceLang + "&text=" + sourceString + "&multires=1");
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