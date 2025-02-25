import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/* 
 * This program identifies the top 3 trending hashtags from a dataset of tweets.
 * It reads tweets, extracts hashtags, counts their occurrences in February 2024,
 * sorts them by frequency, and displays the top 3 trending hashtags in tabular format.
 * 
 * Approach:
 * 1. Read the tweet input from the user.
 * 2. Split the tweet data into four parts: tweet_id, user_id, tweet_text, and tweet_date.
 * 3. Extract hashtags from the tweet_text using regular expressions.
 * 4. Count the occurrences of each hashtag and store them in a HashMap.
 * 5. Filter tweets to consider only those from February 2024.
 * 6. Sort the hashtags first by their count in descending order, then by the hashtag name in descending order.
 * 7. Display the top 3 hashtags in a formatted tabular output.
 */

public class TopTrendingHashtag {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  // Initialize scanner for user input
        List<String[]> tweets = new ArrayList<>(); // List to store the tweets input by the user

        // Prompt user to input the number of tweets
        System.out.print("Enter the number of tweets: ");
        int tweetCount = scanner.nextInt();  // Number of tweets to process
        scanner.nextLine(); // Consume the newline after the number input
        
        // Loop to input tweet details
        for (int i = 0; i < tweetCount; i++) {
            System.out.println("Enter tweet " + (i + 1) + " (Format: tweet_id user_id tweet_text tweet_date):");
            String tweetInput = scanner.nextLine();  // Read the input tweet
            String[] tweetData = tweetInput.split("\\s+", 3);  // Split input into tweet_id, user_id, and tweet_text+tweet_date
            if (tweetData.length < 3) {  // Check if the split has enough parts (i.e., 3 parts)
                System.out.println("Invalid format! Please enter in the correct format.");
                i--;  // Retry input if the format is wrong
                continue;
            }

            // Extract the last space-separated part as the tweet date
            int lastSpaceIndex = tweetData[2].lastIndexOf(" ");  // Find the last space in tweet_text+date to separate the date
            if (lastSpaceIndex == -1) {  // If no space found, the date is missing
                System.out.println("Invalid format! Date missing.");
                i--;  // Retry input
                continue;
            }
            // Extract tweet text and tweet date
            String tweetText = tweetData[2].substring(0, lastSpaceIndex);  // Extract the tweet text part
            String tweetDate = tweetData[2].substring(lastSpaceIndex + 1);  // Extract the date part
            tweets.add(new String[]{tweetData[0], tweetData[1], tweetText, tweetDate});  // Add the processed tweet to the list
        }

        Map<String, Integer> hashtagCount = new HashMap<>();  // Initialize a map to store hashtag counts
        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_]+)");  // Regex pattern to match valid hashtags (alphanumeric + underscores)

        // Process each tweet to extract hashtags and count them
        for (String[] tweetData : tweets) {
            String tweet = tweetData[2];  // Extract tweet text
            String date = tweetData[3];  // Extract tweet date

            // Only consider tweets from February 2024
            if (date.startsWith("2024-02")) {
                Matcher matcher = hashtagPattern.matcher(tweet);  // Create a matcher to find hashtags in the tweet
                while (matcher.find()) {  // Loop through each hashtag found in the tweet
                    String hashtag = matcher.group(1);  // Extract the hashtag without the '#' symbol
                    // Increment the count of this hashtag in the map
                    hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
                }
            }
        }

        // If no hashtags are found for February 2024, display a message and stop
        if (hashtagCount.isEmpty()) {
            System.out.println("\nNo trending hashtags found for February 2024.");
            return;  // Exit the program if no hashtags were found
        }

        // Sort hashtags: First by count in descending order, then by name in descending order
        List<Map.Entry<String, Integer>> topHashtags = hashtagCount.entrySet()
                .stream()  // Convert map entries to a stream for processing
                .sorted((a, b) -> {
                    if (!a.getValue().equals(b.getValue())) {
                        return b.getValue() - a.getValue();  // Sort by count in descending order
                    }
                    return b.getKey().compareTo(a.getKey());  // If counts are the same, sort by hashtag name in descending order
                })
                .limit(3)  // Take only the top 3 hashtags
                .collect(Collectors.toList());  // Collect the sorted results into a list

        // Print the result in a tabular format
        System.out.println("\n+------------+--------+");
        System.out.println("| Hashtag    | Count  |");
        System.out.println("+------------+--------+");
        // Loop through the top 3 hashtags and display them in the table
        for (Map.Entry<String, Integer> entry : topHashtags) {
            System.out.printf("| #%-10s | %5d |\n", entry.getKey(), entry.getValue());  // Format and print each entry
        }
        System.out.println("+------------+--------+");

        scanner.close();  // Close the scanner to release resources
    }
}

/* Testing Results
    Example 1:

    Enter the number of tweets: 7
    Enter tweet 1 (Format: tweet_id user_id tweet_text tweet_date):
    135 13 Enjoying a great start to the day. #HappyDay #MorningVibes 2024-02-01
    Enter tweet 2 (Format: tweet_id user_id tweet_text tweet_date):
    136 14 Another #HappyDay with good vibes! #FeelGood 2024-02-03
    Enter tweet 3 (Format: tweet_id user_id tweet_text tweet_date):
    137 15 Productivity peaks! #WorkLife #ProductiveDay 2024-02-04
    Enter tweet 4 (Format: tweet_id user_id tweet_text tweet_date):
    138 16 Exploring new tech frontiers. #TechLife #Innovation 2024-02-04
    Enter tweet 5 (Format: tweet_id user_id tweet_text tweet_date):
    139 17 Gratitude for today's moments. #HappyDay #Thankful 2024-02-05
    Enter tweet 6 (Format: tweet_id user_id tweet_text tweet_date):
    140 18 Innovation drives us. #TechLife #FutureTech 2024-02-07
    Enter tweet 7 (Format: tweet_id user_id tweet_text tweet_date):
    141 19 Connecting with nature's serenity. #Nature #Peaceful 2024-02-09

    +------------+--------+
    | Hashtag    | Count  |
    +------------+--------+
    | #HappyDay   |     3 |
    | #TechLife   |     2 |
    | #WorkLife   |     1 |
    +------------+--------+

    Example 2: 
    Enter the number of tweets: 8
    Enter tweet 1 (Format: tweet_id user_id tweet_text tweet_date):
    201 21 Just finished a great book! #Reading #BookLover 2024-02-02
    Enter tweet 2 (Format: tweet_id user_id tweet_text tweet_date):
    202 22 A sunny day at the park. #Nature #Peaceful 2024-02-03
    Enter tweet 3 (Format: tweet_id user_id tweet_text tweet_date):
    203 23 Coding all night. #DeveloperLife #TechLife 2024-02-04
    Enter tweet 4 (Format: tweet_id user_id tweet_text tweet_date):
    204 24 Learning new programming tricks. #Coding #TechLife 2024-02-05
    Enter tweet 5 (Format: tweet_id user_id tweet_text tweet_date):
    205 25 Morning coffee is the best! #CoffeeLover #MorningVibes 2024-02-06
    Enter tweet 6 (Format: tweet_id user_id tweet_text tweet_date):
    206 26 Inspired to write today. #Writing #Creativity 2024-02-07
    Enter tweet 7 (Format: tweet_id user_id tweet_text tweet_date):
    207 27 Exploring AI advancements. #MachineLearning #AI 2024-02-08
    Enter tweet 8 (Format: tweet_id user_id tweet_text tweet_date):
    208 28 The future is tech! #AI #TechLife 2024-02-09

    +------------+--------+
    | Hashtag    | Count  |
    +------------+--------+
    | #TechLife   |     3 |
    | #AI         |     2 |
    | #Writing    |     1 |
    +------------+--------+

 */
