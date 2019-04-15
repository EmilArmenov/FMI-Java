package bg.sofia.uni.fmi.mjt.git;

import java.util.*;

public class Repository {

    private Map<String, Boolean> stage;
    private Map<String, LinkedList<Commit>> commitHistory;
    private Set<String> removedFilesBeforeCommit;
    private String currentBranch;
    private String head;
    private int changedFilesCounter;

    /**
     * Each file in the stage is associated with true or false value
     * false represents that the file won't change the
     * @changedFilesCounter even if it gets removed otherwise it is true
     *
     * @removedFilesBeforeCommit stores files that were removed before creating a commit
     * so when a file that was already added and then removed from the stage adding that file again
     * before creating a commit will not affect the @changedFilesCounter
     */
    public Repository() {
        stage = new HashMap<>();
        commitHistory = new HashMap<>();
        removedFilesBeforeCommit = new HashSet<>();
        commitHistory.put("master", new LinkedList<>());
        currentBranch = "master";
        head = currentBranch;
        changedFilesCounter = 0;
    }

    public Result add(String... files) {
        for (String file: files) {
            if (stage.containsKey(file)) {
                return new Result("'" + file + "' already exists", false);
            }
        }
        StringBuilder message = new StringBuilder("added ");

        for (int i = 0; i < files.length; i++) {
            message.append(files[i]);
            if (!removedFilesBeforeCommit.contains(files[i])) {
                changedFilesCounter++;
            }

            stage.put(files[i], false);
            if (i < files.length - 1) {
                message.append(", ");
            }
        }
        message.append(" to stage");

        return new Result(message.toString(), true);
    }

    public Result commit(String message) {
        if (changedFilesCounter == 0) {
            return new Result("nothing to commit, working tree clean", false);
        } else {
            commitHistory.get(currentBranch).addFirst(new Commit(message));
            setFilesValueTrue();
            String returnMessage = changedFilesCounter + " files changed";
            changedFilesCounter = 0;
            removedFilesBeforeCommit.clear();
            return new Result(returnMessage, true);
        }
    }

    /**
     * This method is called after every commit and sets every value of
     * the files in the stage to true indicating that if any of them gets deleted
     * the @changedFilesCounter will increment
     */
    private void setFilesValueTrue() {
        Iterator it = stage.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            pair.setValue(true);
        }
    }

    public Result log() {
        if (commitHistory.get(currentBranch).isEmpty()) {
            return new Result("branch " + currentBranch + " does not have any commits yet", false);
        } else {
            StringBuilder message = new StringBuilder();
            if (currentBranch.equals(head)) {
                for (Commit element : commitHistory.get(currentBranch)) {
                    message.append(element.toString());
                    message.append("\n\n");
                }
            } else {
                boolean canAppend = false;
                for (Commit element : commitHistory.get(currentBranch)) {
                    if (element.getHash().equals(head)) {
                        canAppend = true;
                    }
                    if (canAppend) {
                        message.append(element.toString());
                        message.append("\n\n");
                    }
                }
            }
            message.deleteCharAt(message.length() - 1);
            message.deleteCharAt(message.length() - 1);
            return new Result(message.toString(), true);
        }
    }

    public Commit getHead() {
        return (!commitHistory.get(currentBranch).isEmpty()) ? commitHistory.get(currentBranch).getFirst() : null;
    }

    public String getBranch() {
        return currentBranch;
    }

    public Result createBranch(String name) {
        if (commitHistory.containsKey(name)) {
            return new Result("branch " + name + " already exists", false);
        } else {
            commitHistory.put(name, new LinkedList<>());
            for (Commit element: commitHistory.get(currentBranch)) {
                commitHistory.get(name).addLast(element);
            }
            return new Result("created branch " + name, true);
        }
    }

    public Result checkoutBranch(String name) {
        if (!commitHistory.containsKey(name)) {
            return new Result("branch " + name + " does not exist", false);
        } else {
            currentBranch = name;
            head = currentBranch;
            return new Result("switched to branch " + name, true);
        }
    }

    public Result checkoutCommit(String hash) {
        for (Commit element: commitHistory.get(currentBranch)) {
            if (element.getHash().equals(hash)) {
                head = element.getHash();

                return new Result("HEAD is now at " + element.getHash(), true);
            }
        }

        return new Result("commit " + hash + " does not exist", false);
    }

    public Result remove(String... files) {
        for (String file: files) {
            if (!stage.containsKey(file)) {
                return new Result("'" + file + "' did not match any files", false);
            }
        }
        StringBuilder message = new StringBuilder("added ");

        for (int i = 0; i < files.length; i++) {
            message.append(files[i]);
            removedFilesBeforeCommit.add(files[i]);
            if (stage.remove(files[i])) {
                changedFilesCounter++;
            }
            if (i < files.length - 1) {
                message.append(", ");
            }
        }
        message.append(" for removal");
        return new Result(message.toString(), true);
    }
}