//import java.util.*;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import net.sf.json.*;


public class translator {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Example:\n translator hi en ru");
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
		
		System.out.println(simpleTranslate + "\n" + translateDetails);
	}
}