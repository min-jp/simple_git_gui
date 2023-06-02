package gui.branchpanel.component.commit;

import file.GitBranchData;
import jgitmanager.CommitInfo;
import jgitmanager.JGitManagerImprv;

import javax.swing.*;
import java.awt.*;

public class GitCommitStatus extends JPanel {
    private JLabel CheckSum;
    private JLabel CommitMessage;
    private JLabel AuthorName;
    private JLabel AuthorEmail;
    private JLabel Date;
    private CommitInfo commitInfo;
    private GitBranchData gitBranchData;
    public GitCommitStatus(GitBranchData gitBranchData){


        try{
            commitInfo = JGitManagerImprv.gitCommitInfo(gitBranchData.getSelectedCommit());

        }catch (Exception e){
            System.out.println(e.toString());
        }

        JPanel CommitDetailLabel=new JPanel(new GridLayout(0,1,2,2));
        this.add(CommitDetailLabel,BorderLayout.WEST);

        JPanel CommitDetailValue=new JPanel(new GridLayout(0,1,2,2));
        this.add(CommitDetailValue,BorderLayout.CENTER);

        CommitDetailLabel.add(new JLabel("CHECKSUM",JLabel.TRAILING));
        /*COMMIT 내용 입력*/
        CheckSum=new JLabel(commitInfo.getCheckSum());
        CommitDetailValue.add(CheckSum);

        CommitDetailLabel.add(new JLabel("COMMIT MESSAGE",JLabel.TRAILING));
        //COMMIT MESSAGE
        CommitMessage=new JLabel(commitInfo.getCommitMessage());
        CommitDetailValue.add(CommitMessage);

        CommitDetailLabel.add(new JLabel("AUTHOR",JLabel.TRAILING));
        //AUTHOR
        AuthorName=new JLabel(commitInfo.getAuthorName());
        CommitDetailValue.add(AuthorName);
        CommitDetailLabel.add(new JLabel("AUTHOR EMAIL",JLabel.TRAILING));
        //EMAIL
        AuthorEmail=new JLabel(commitInfo.getAuthorEMail());
        CommitDetailValue.add(AuthorEmail);

        CommitDetailLabel.add(new JLabel("DATE",JLabel.TRAILING));
        Date=new JLabel(commitInfo.getCommitTime());
        CommitDetailValue.add(Date);
        //DATE



    }


}
