import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

public class FlightTimeCalculator {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java FlightTimeCalculator <path_to_Tickets.json>");
			return;
		}
		try {
			
			JSONArray tickets = readJsonArray(args[0]);
			int totalFlightTime = 0;
			int numFlights = 0;
			List<Integer> flightTimes = new ArrayList<>();
			for (int i = 0; i < tickets.length(); i++) {
				JSONObject ticket = tickets.getJSONObject(i);
				if ("VVO".equals(ticket.getString("origin")) && "TLV".equals(ticket.getString("destination"))) {
					String departureDateTime = ticket.getString("departure_date") + " " + ticket.getString("departure_time");
					String arrivalDateTime = ticket.getString("arrival_date") + " " + ticket.getString("arrival_time");
					int flightTime = getTimeDiff(departureDateTime, arrivalDateTime);
					totalFlightTime += flightTime;
					numFlights++;
					flightTimes.add(flightTime);
				}
			}	
			if (numFlights == 0) {
				System.out.println("No flights found between Vladivostok and Tel Aviv.");
			} else {
				double avgFlightTime = (double) totalFlightTime / numFlights;
				System.out.printf("Average flight time between Vladivostok and Tel Aviv: %.2f hours\n", avgFlightTime);
				Collections.sort(flightTimes);
				double percentile90 = getPercentile(flightTimes, 90);
				System.out.printf("90th percentile flight time between Vladivostok and Tel Aviv: %.2f hours\n", percentile90);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JSONArray readJsonArray(String filePath) throws Exception {
		File file = new File(filePath);
		byte[] bytes = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
		fis.close();
		String jsonStr = new String(bytes, "UTF-8");
		JSONObject jsonObj = new JSONObject(jsonStr);
		return jsonObj.getJSONArray("tickets");
	}

	private static int getTimeDiff(String dateTime1, String dateTime2) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
		Date date1 = format.parse(dateTime1);
		Date date2 = format.parse(dateTime2);
		long diffInMillis = date2.getTime() - date1.getTime();
		return (int) (diffInMillis / (60 * 60 * 1000));
	}

	private static double getPercentile(List<Integer> values, int percentile) {
		int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
		return values.get(index);
	}
}