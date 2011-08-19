//import java.util.*;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import net.sf.json.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URL;


public class translator {
	public static void main(String[] args) throws Exception {
		PopupMenu popup = new PopupMenu();
		
		MenuItem exitItem = new MenuItem("exit");
		MenuItem translateItem = new MenuItem("translate");

	    exitItem.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            System.exit(0);
	        }
	    });
	    
	    translateItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
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

						  String simpleTranslate = translate_data.getJSONArray(0).getString(0).replaceAll("[\\[\"\\]]", "").replace(","," | ");
						  String translateDetails = translate_data.getString(1)
							.replaceAll("[\\[\"\\]]", "")
								.replaceAll("(noun,|verb,|adjective,|interjection,)", "\n$1\n")
									.replace(",",", ").replaceAll("(^, )", "");
						
						  
						  System.out.println(simpleTranslate + "\n" + translateDetails);
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
			}
		});
	    
	    popup.add(exitItem);
	    popup.add(translateItem);

	    SystemTray systemTray = SystemTray.getSystemTray();

	    Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

	    TrayIcon trayIcon = new TrayIcon(image, "hi there", popup);

	    trayIcon.setImageAutoSize(true);

	    systemTray.add(trayIcon);
	    
		// translate
/*		if (args.length < 3) {
			System.out.println("Example:\n java translator hi en ru");
			return;
		}
		

		
		String sourceString = URLEncoder.encode(args[0]);
		String fromLang = args[1];
		String toLang = args[2];
		
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

		String simpleTranslate = translate_data.getJSONArray(0).getString(0).replaceAll("[\\[\"\\]]", "").replace(","," | ");
		String translateDetails = translate_data.getString(1)
			.replaceAll("[\\[\"\\]]", "")
				.replaceAll("(noun,|verb,|adjective,|interjection,)", "\n$1\n")
					.replace(",",", ").replaceAll("(^, )", "");
		
		trayIcon.setToolTip(simpleTranslate + "\n" + translateDetails);
		
		System.out.println(simpleTranslate + "\n" + translateDetails);*/
	}
}