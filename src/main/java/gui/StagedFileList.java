package gui;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import file.SelectedFile;
import jgitmanager.FileStatus;
import jgitmanager.JGitManager;
import org.eclipse.jgit.api.errors.GitAPIException;

public class StagedFileList extends JScrollPane {
    private static StagedFileList instance = null;

    private JTable table;
    private StagedFileTableModel stagedFileTableModel;
    boolean cellSizesSet = false;

    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    public StagedFileList() {
        table = new JTable();

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        this.setViewportView(table);
        Dimension d = getPreferredSize();
        setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
    }

    public static StagedFileList getInstance() {
        if(instance == null) {
            instance = new StagedFileList();
        }
        return instance;
    }

    public void setStagedFileTableData() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File[] files = getStagedFile();

                if (stagedFileTableModel == null) {
                    stagedFileTableModel = new StagedFileTableModel();
                    table.setModel(stagedFileTableModel);
                }

                stagedFileTableModel.setFiles(files);
                if (!cellSizesSet) {
                    table.setRowHeight(23);

                    setColumnWidth(0, 100);
                    table.getColumnModel().getColumn(0).setMaxWidth(200);
                    setColumnWidth(1, table.getRowHeight() * 3 + 5);

                    cellSizesSet = true;
                }

                GitGUI.gui.repaint();
            }
        });
    }

    private File[] getStagedFile() {
        Set<String> stagedFileSet;
        File[] stagedFiles;
        File selectedFile = SelectedFile.getInstance().getFile();

        try {
            if(selectedFile != null) {
                stagedFileSet = JGitManager.gitStagedList(selectedFile);
            } else {
                stagedFileSet = new HashSet<>();
            }
        } catch(NullPointerException n) {
            stagedFileSet = new HashSet<>();
        } catch(IOException | GitAPIException e) {
            stagedFileSet = new HashSet<>();
        }

        stagedFiles = new File[stagedFileSet.size()];

        int i = 0;
        for(String filePath : stagedFileSet) {
            //filePath가 repo 기준 상대경로이므로, 다시확인
            String dotGit = JGitManager.getRepositoryAbsolutePath(selectedFile);
            File repo = (new File(dotGit)).getParentFile();
            stagedFiles[i] = new File(repo,filePath);
            i++;
        }

        return stagedFiles;
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }
}

class StagedFileTableModel extends AbstractTableModel {
    private File[] stagedFiles;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
            "File",
            "File Status",
            "Path"
    };

    public StagedFileTableModel() {
        this(new File[0]);
    }

    StagedFileTableModel(File[] files) {
        this.stagedFiles = files;
    }

    public Object getValueAt(int row, int column) {
        File file = stagedFiles[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemDisplayName(file);
            case 1:
                String gitStatusImagePath = getGitStatusImagePath(file);
                if (gitStatusImagePath != null) {
                    URL url = getClass().getResource(gitStatusImagePath);
                    if (url != null) {
                        ImageIcon icon = new ImageIcon(url);
                        int cellHeight = fileSystemView.getSystemIcon(file).getIconHeight() + 6;
                        int cellWidth = cellHeight * 3;
                        Image image = icon.getImage();
                        Image scaledImage = image.getScaledInstance(cellWidth, cellHeight, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                        return scaledIcon;
                    }
                }
                return null;
            case 2:
                return file.getPath();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch(column) {
            case 1:
                return ImageIcon.class;
            default:
                return String.class;
        }
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return stagedFiles.length;
    }

    public File getFile(int row) {
        return stagedFiles[row];
    }

    public void setFiles(File[] files) {
        this.stagedFiles = files;
        fireTableDataChanged();
    }

    public String getGitStatusImagePath(File file) {
        FileStatus fileStatus;
        String imagePath;

        if(file.isDirectory()) {
            imagePath = "/git_status_icons/Folder.png";
        }
        else {
            try {
                if(JGitManager.findGitRepository(file) == 1) {
                    fileStatus = JGitManager.gitCheckFileStatus(file);
                }
                else {
                    fileStatus = FileStatus.UNTRACKED;
                }

                switch (fileStatus) {
                    case FOLDER:
                        imagePath = "/git_status_icons/Folder.png";
                        break;
                    case UNTRACKED:
                        imagePath = "/git_status_icons/Untracked.png";
                        break;
                    case MODIFIED:
                        imagePath = "/git_status_icons/Modified.png";
                        break;
                    case STAGED_MODIFIED:
                        imagePath = "/git_status_icons/Staged_Modified.png";
                        break;
                    case DELETED:
                        imagePath = "/git_status_icons/Deleted.png";
                        break;
                    case STAGED:
                        imagePath = "/git_status_icons/Staged.png";
                        break;
                    case UNMODIFIED:
                        imagePath = "/git_status_icons/Committed.png";
                        break;
                    default:
                        imagePath = null;
                }
            } catch(IOException | GitAPIException e) {
                imagePath = null;
            };
        }
        return imagePath;
    }
}