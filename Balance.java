import java.io.*;
import java.util.*;
public class Balance {
    public static final String DELIMITER = ",";

    Map<String, Integer> nameToIndex;
    Map<Integer, String> indexToName;

    // Use PriorityQueue to sort transactions,  the latest transaction is at the peek()
    // Use name to break a tie
    PriorityQueue<String[]> transactions;

    // Use stack to store transaction from oldest to newest;
    Stack<String[]> inorder;
    // Store the maximum amount price can be deducted from user at each transaction
    Map<String, Stack<Integer>> nameToOpeartion;

    Balance() {
        nameToIndex = new HashMap<>();
        indexToName = new HashMap<>();
        transactions = new PriorityQueue<>((a, b) -> (a[2].equals(b[2])) ? a[0].compareTo(b[0]): b[2].compareTo(a[2]));
        inorder = new Stack<>();
        nameToOpeartion = new HashMap<>();
    }

    private void CVSReader() throws IOException {
        FileReader fr = new FileReader(new File("transactions.csv"));
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        // filter the first line
        line =  br.readLine();
        int index = 0;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(DELIMITER);

            if (!nameToIndex.containsKey(items[0])) {
                nameToIndex.put(items[0], index);
                indexToName.put(index++, items[0]);
            }
            transactions.offer(items);

        }
        br.close();
    }

    // Calculate maximum amount price can be deducted from user at each transaction
    // Start from the latest transaction to ensure no negative points
    // Store the result in HashMap (nameToOpeartion)
    private void initial() {
        HashMap<String, Integer> nameToSpend = new HashMap<>();
        while (!transactions.isEmpty()) {
            String[] curr = transactions.poll();
            String name = curr[0], value = curr[1];
            inorder.push(new String[]{name, value});

            int val = Integer.valueOf(value);
            if (!nameToOpeartion.containsKey(name)) {
                nameToOpeartion.putIfAbsent(name, new Stack<>());
                nameToSpend.put(name, nameToSpend.getOrDefault(name, 0));
            }
            Stack<Integer> temp = nameToOpeartion.get(name);
            if (val < 0) {
                nameToSpend.put(name, nameToSpend.get(name) + val);
            } else{
               int later = nameToSpend.get(name);
               nameToSpend.put(name, Math.min(later + val, 0));
               temp.push(Math.max(0, later + val));
            }
            nameToOpeartion.put(name, temp);

        }
    }

    // Simulate the process of spending points starting from the oldest points
    private int[] process(int total) {
        int n = nameToIndex.size();
        int[] res = new int[n];
        while (!inorder.isEmpty()) {
            String[] curr = inorder.pop();
            String name = curr[0];
            int val = Integer.valueOf(curr[1]), index = nameToIndex.get(name);
            // When the previous spend can not cover the amount of points to spend
            // If we can get points from this transaction, update the left amount of points
            res[index] += val;
            if (total > 0) {
                if (val <= 0) continue;
                Stack<Integer> temp = nameToOpeartion.get(name);
                if (temp.isEmpty()) continue;
                int maximumTransactionPrice = temp.pop();
                res[index] -= total > maximumTransactionPrice ? maximumTransactionPrice : total;
                total -= maximumTransactionPrice;
            }

        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        Balance b= new Balance();
        b.CVSReader();
        b.initial();
        int[] res = b.process(Integer.valueOf(args[0]));
        for (int i = 0; i < res.length; i++) {
            String name = b.indexToName.get(i);
            System.out.println(name + ": " + res[i]);
        }
    }
}
