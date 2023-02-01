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
    Map<String, Stack<Integer>> balance;

    Balance() {
        nameToIndex = new HashMap<>();
        indexToName = new HashMap<>();
        transactions = new PriorityQueue<>((a, b) -> (a[2].equals(b[2])) ? a[0].compareTo(b[0]): b[2].compareTo(a[2]));
        inorder = new Stack<>();
        balance = new HashMap<>();
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
    // Start from the latest transaction to ensure no negative balance
    // Store the result in HashMap (balance)
    private void initial() {
        while (!transactions.isEmpty()) {
            String[] curr = transactions.poll();
            String name = curr[0], value = curr[1];
            inorder.push(new String[]{name, value});

            int val = Integer.valueOf(value);
            if (!balance.containsKey(name)) {
                balance.putIfAbsent(name, new Stack<>());
                balance.get(name).add(-val);
            } else {
                Stack<Integer> temp = balance.get(name);
                if (val < 0) temp.add(-val);
                else temp.add(-val + Math.max(0, temp.peek()));
                balance.put(name, temp);
            }
        }
    }

    // Simulate the process of spending points starting from the oldest points
    private int[] process(int total) {
        int n = nameToIndex.size();
        int[] res = new int[n];
        while (!inorder.isEmpty()) {
            String[] curr = inorder.pop();
            String name = curr[0];
            int val = balance.get(name).pop(), index = nameToIndex.get(name);
            // When the previous spend can not cover the amount of points to spend
            // If we can get points from this transaction, update the left amount of points
            if (total > 0) {
                if (val >= 0) continue;
                if (total + val < 0) {
                    res[index] += Integer.valueOf(curr[1]) - total;
                }
                total += val;
            } else {
                // If the total amount of points can be obtained from previous transactions
                // For the latter transactions, the points is used to update payer account balance
                res[index] += Integer.valueOf(curr[1]);
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
