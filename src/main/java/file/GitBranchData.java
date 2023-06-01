package file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GitBranchData {
    private List<BranchDataChangeListener> listeners;

    private String CurrentBranch;
    private String SelectedBranch;
    private String SelectedCommit; //아마 RevCommit 객체로 바뀔 예정
    private File SelectedChangeFile;

    public GitBranchData() {
        listeners = new ArrayList<>();
    }

    public void addBranchDataChangeListener(BranchDataChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyBranchDataChange() {
        for(BranchDataChangeListener listener : listeners) {
            listener.updateData();
        }
    }

    public String getCurrentBranch() {
        return CurrentBranch;
    }

    public void setCurrentBranch(String branch) {
        CurrentBranch = branch;
        notifyBranchDataChange();
    }

    public String getSelectedBranch() {
        return SelectedBranch;
    }

    public void setSelectedBranch(String branch) {
        SelectedBranch = branch;
        notifyBranchDataChange();
    }

    public String getSelectedCommit() {
        return SelectedCommit;
    }

    public void setSelectedCommit(String commit) {
        SelectedCommit = commit;
        notifyBranchDataChange();
    }

    public File getSelectedChangeFile() {
        return SelectedChangeFile;
    }

    public void setSelectedChangeFile(File file) {
        SelectedChangeFile = file;
        notifyBranchDataChange();
    }
}