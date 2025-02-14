/* Question no: 4,a
 * To identify the top 3 trending hashtags from a dataset of tweets at  the tweeta from february 2024 is filtered based 
 * on the tweet_date column and then hashtag is extracted using an expression i.e "#(\\w+)". Then the HashMap is 
 * used for storing each hashtag and its count. A HashMap stores each hashtag and its count. After that the 
 * top 3 hashtags are selected and displayed in a tabular format.
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TopTrendingHashtag {
    public static void main(String[] args) {  //main method
        List<String[]> tweets = Arrays.asList(   //sample of dataset
            new String[]{"135", "13", "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01"},
            new String[]{"136", "14", "Another #HappyDay with good vibes! #FeelGood", "2024-02-03"},
            new String[]{"137", "15", "Productivity peaks! #WorkLife #ProductiveDay", "2024-02-04"},
            new String[]{"138", "16", "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-04"},
            new String[]{"139", "17", "Gratitude for today's moments. #HappyDay #Thankful", "2024-02-05"},
            new String[]{"140", "18", "Innovation drives us. #TechLife #FutureTech", "2024-02-07"},
            new String[]{"141", "19", "Connecting with nature's serenity. #Nature #Peaceful", "2024-02-09"}

        );

        Map<String, Integer> hashtagCount = new HashMap<>();  //Initialization of  a map to store hashtag counts

        Pattern hashtagPattern = Pattern.compile("#(\\w+)");   //Defining a regex pattern for extraction of hashtags from tweets

        for (String[] tweetData : tweets){  //for each loop through each tweet in order to extract hashtags from February 2024
            String tweet = tweetData[2];    // Extracting the tweet text
            String date = tweetData[3];     // Extracting the tweet date

            if(date.startsWith("2024-02")){   // if condition to check if the tweet is from February 2024
                Matcher matcher = hashtagPattern.matcher(tweet);
                while (matcher.find()) { 
                    String hashtag = matcher.group(1);   // Extracting the hashtag text (without the #)
                    hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);  //Increasing the count of the hashtag in the HashMap
                }

            }
        }

        List<Map.Entry<String, Integer>> topHashtags = hashtagCount.entrySet() //Sorting hashtags based on count (descending), then by name (descending)
          .stream()
          .sorted((a,b) -> {
            if(!a.getValue().equals(b.getValue())){
                return  b.getValue() - a.getValue();
            }
            return  b.getKey().compareTo(a.getKey());
          })
          .limit(3)  // Selecting only the top 3 hashtags
          .collect(Collectors.toList());

        System.out.println("+------------+--------+");    //Printing the output in tabular format
        System.out.println("| hashtag    | count  |");
        System.out.println("+------------+--------+");
        for (Map.Entry<String, Integer> entry : topHashtags) {
            System.out.printf("| #%-10s | %5d |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+------------+--------+");
    }
}

/* Testing Result
    +------------+--------+
    | hashtag    | count  |
    +------------+--------+
    | #HappyDay   |     3 |
    | #TechLife   |     2 |
    | #WorkLife   |     1 |
    +------------+--------+
 */


