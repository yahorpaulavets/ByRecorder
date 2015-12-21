package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.yahor.gobrotium.utils.FileUtils;
import org.yahor.gobrotium.utils.L;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JTestRecordingFrame extends JFrame implements IHistoryItemListener, TreeSelectionListener, MouseListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 900;

    private JTree tree;
    private DefaultMutableTreeNode testSuiteRootNode;
    //    private DefaultMutableTreeNode testCase;
    private ImageIcon playedSuccessIcon;
    private ImageIcon playedFailIcon;
    private ImageIcon readyIcone;
    private ImageIcon playedReadyIcon;
    private File mReportFile;
    private TestCaseItem itemToBeReplaced = null;
    private DefaultMutableTreeNode nodeToBeUpdated = null;
    private boolean shouldStopRecording = false;
    private HashMap<Long, TestCase> suite;
    private ArrayList<DefaultMutableTreeNode> currentTestCaseNodes;

    private JTestRecordingFrame() {
        initUI();
    }

    private void initUI() {
        testSuiteRootNode = new DefaultMutableTreeNode(new TestSuiteUserObject("Test suite"));
        createNodes(testSuiteRootNode);
        try {
            playedSuccessIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/green.png")));
            playedFailIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/red.png")));
            playedReadyIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/yellow.png")));
            readyIcone = new ImageIcon(ImageIO.read(getClass().getResource("/resources/grey.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create a tree that allows one selection at a time.
        tree = new JTree(testSuiteRootNode);
        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean isLeaf, int row, boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);

                if(value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode item = (DefaultMutableTreeNode) value;

                    if(!(item.getUserObject() instanceof TestStepUserObject)) {
                        if(item.getUserObject() != null && item.getUserObject() instanceof TestCaseUserObject) {
                            TestCaseUserObject testCaseUserObject = (TestCaseUserObject) item.getUserObject();
                            c.setEnabled(RecordingManager.getInstance().getTestCase(testCaseUserObject.getTestCaseId()).isEnabled());
                        }
                        return c;
                    }

                    TestStepUserObject item2 = (TestStepUserObject) item.getUserObject();
                    TestCaseItem item3 = item2.getItem();

                    if(item3.isPlayed()) {
                        switch (item3.isPassed()) {
                            case OK:
                                setIcon(playedSuccessIcon);
                                break;
                            case FAIL:
                                setIcon(playedFailIcon);
                                break;
                            case BLIND_STEP:
                                setIcon(playedReadyIcon);
                                break;
                        }

                    } else {
                        setIcon(readyIcone);
                    }
                    c.setEnabled(RecordingManager.getInstance().getTestCase(item2.getParentId()).isEnabled());
                }


                return c;
            }
        });

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(this);
        if(tree.getRowCount() > 0) {
            for(int i = 0;  i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        }

        tree.updateUI();

        JScrollPane scrollPane = new JScrollPane(tree);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(5, 5, WIDTH, HEIGHT);
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        contentPane.add(scrollPane);
        setContentPane(contentPane);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void createNodes(DefaultMutableTreeNode top) {
        suite = RecordingManager.getInstance().getCurrentTestSuite();
        currentTestCaseNodes = new ArrayList<DefaultMutableTreeNode>(100);

        for(Long id: suite.keySet()) {
            DefaultMutableTreeNode testCase = new DefaultMutableTreeNode(new TestCaseUserObject("Test case", id));
            top.add(testCase);
            currentTestCaseNodes.add(testCase);
            if(!RecordingManager.getInstance().isTestRecording()) {
                createTestCaseStepNodes(id, suite.get(id));
            }
        }
    }

    private void createTestCaseStepNodes(Long id, TestCase testCase1) {
        for(TestCaseItem step: testCase1.getTestCaseSteps()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestStepUserObject(step, id));
            getTestCaseNode(id).add(node);
        }
    }

    @Override
    public void onHistoryItemRecorded(long id, TestCaseItem item) {
        if(itemToBeReplaced != null) {
            nodeToBeUpdated.setUserObject(new TestStepUserObject(item, id));
            RecordingManager.getInstance().replaceItem(itemToBeReplaced, item);
            itemToBeReplaced = null;
            if(shouldStopRecording) {
                RecordingManager.getInstance().setTestRecording(false);
                shouldStopRecording = false;
            }
        } else {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestStepUserObject(item, id));
            getTestCaseNode(id).add(node);
        }

        if(!tree.isExpanded(1)) {
            tree.expandRow(1);
        }

        tree.updateUI();
    }

    private DefaultMutableTreeNode getTestCaseNode(long id) {
        for(DefaultMutableTreeNode node: currentTestCaseNodes) {
            TestCaseUserObject obj = (TestCaseUserObject) node.getUserObject();
            if(obj.getTestCaseId() == id) {
                return node;
            }
        }

        return null;
    }

    @Override
    public void onHistoryItemPlayed(long id, TestCaseItem touchTestCaseItem) {
        FileUtils.writeText(mReportFile, L.getDate(System.currentTimeMillis()) + "] " + touchTestCaseItem.toString(), true);

        long eventOccurredTime = touchTestCaseItem.getOccurred();
        for(int index = 0; index < getTestCaseNode(id).getChildCount(); index++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) getTestCaseNode(id).getChildAt(index);
            TestStepUserObject item =  (TestStepUserObject)node.getUserObject();
            if(eventOccurredTime == item.getItem().getOccurred()) {

            }
        }
    }

    @Override
    public void onTestSuiteLoaded() {
        initUI();
    }

    private static JTestRecordingFrame instance;
    public static JTestRecordingFrame getInstance() {
        if(instance == null) {
            instance = new JTestRecordingFrame();
        }
        instance.setVisible(true);
        return instance;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        myPopupEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void myPopupEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        JTree tree = (JTree)e.getSource();
        TreePath path = tree.getPathForLocation(x, y);
        if (path == null)
            return;

        tree.setSelectionPath(path);

        final DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();

        if(clickedNode.getUserObject() instanceof TestSuiteUserObject) {
            L.e("TEST SUITE IS CLICKED");
        } else if(clickedNode.getUserObject() instanceof TestCaseUserObject) {
            L.e("TEST CASE IS CLICKED");
            showTestCasePopupMenu(clickedNode, x ,y);
        } else if(clickedNode.getUserObject() instanceof TestStepUserObject) {
            L.e("TEST STEP IS CLICKED");
            showTestStepPopupMenu(clickedNode, parentNode, x ,y);
        }
    }

    private void showTestCasePopupMenu(final DefaultMutableTreeNode clickedNode, int x, int y) {
        TestCaseUserObject testCaseUserObject = (TestCaseUserObject) clickedNode.getUserObject();
        final long id = testCaseUserObject.getTestCaseId();
        String label = "step: " + clickedNode;

        JPopupMenu popup = new JPopupMenu();
        JMenuItem firstItem = new JMenuItem(label);

        final JMenuItem playItem = new JMenuItem("Play");
        JMenuItem removeItem = new JMenuItem("Remove");
        JCheckBoxMenuItem activateItem = new JCheckBoxMenuItem("Active");
        activateItem.setSelected(true);

        firstItem.setEnabled(false);
        playItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenericDeviceRemoteControl.getInstance().playTestCase(id);
            }
        });

        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = getTestCaseNode(id);
                node.removeAllChildren();

                DefaultTreeModel model = (DefaultTreeModel) (tree.getModel());
                TreePath[] paths = tree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
                    model.removeNodeFromParent(node);
                }

                currentTestCaseNodes.remove(node);
                RecordingManager.getInstance().removeTestCase(id);
                tree.updateUI();
            }
        });

        activateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecordingManager.getInstance().getTestCase(id).setEnabled(((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });

        popup.add(firstItem);
        popup.add(playItem);
        popup.add(removeItem);
        popup.add(activateItem);
        popup.show(tree, x, y);
    }

    private int getTestCaseNodeIndex(long id) {
        for (int i = 0; i < currentTestCaseNodes.size(); i++) {
            DefaultMutableTreeNode node = currentTestCaseNodes.get(i);
            TestCaseUserObject obj = (TestCaseUserObject) node.getUserObject();
            if (obj.getTestCaseId() == id) {
                return i;
            }
        }

        return -1;
    }

    private void showTestStepPopupMenu(final DefaultMutableTreeNode clickedNode, DefaultMutableTreeNode parentNode, int x, int y) {
        TestCaseUserObject testCaseUserObject = (TestCaseUserObject) parentNode.getUserObject();
        final long id = testCaseUserObject.getTestCaseId();

        String label = "step: " + clickedNode;

        TestStepUserObject inputHistoryItem = (TestStepUserObject) clickedNode.getUserObject();
        final TestCaseItem item = inputHistoryItem.getItem();

        JPopupMenu popup = new JPopupMenu();
        JMenuItem firstItem = new JMenuItem(label);
        final JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem removeItem = new JMenuItem("Remove");
        JMenuItem recordItem = new JMenuItem("Record again");

        firstItem.setEnabled(false);

        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { editItem(item, clickedNode, id); }
        });

        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { removeItem(id, item); }
        });

        recordItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { record(item, clickedNode); }
        });

        boolean isEnabled = RecordingManager.getInstance().getTestCase(id).isEnabled();

        editItem.setEnabled(isEnabled);
        removeItem.setEnabled(isEnabled);
        removeItem.setEnabled(isEnabled);

        popup.add(firstItem);
        popup.add(editItem);
        popup.add(removeItem);
        popup.add(recordItem);

        popup.show(tree, x, y);
    }

    private void record(TestCaseItem item, DefaultMutableTreeNode obj) {

        if(RecordingManager.getInstance().isTestPlaying()) {
            // TODO toast
            return;
        }

        if(!RecordingManager.getInstance().isTestRecording()) {
            RecordingManager.getInstance().setTestRecording(true);
            shouldStopRecording = true;
        }

        itemToBeReplaced = item;
        nodeToBeUpdated = obj;
    }

    private void removeItem(long id, TestCaseItem item) {
        RecordingManager.getInstance().removeItem(id, item);
        loadTestCase(RecordingManager.getInstance());
    }

    private void editItem(TestCaseItem item, DefaultMutableTreeNode obj, Long parentId) {
        nodeToBeUpdated = obj;

        if(item instanceof PauseHistoryItem) {
            String name =  JOptionPane.showInputDialog ( "Set integer pause time in seconds" );
            try {
                int i = Integer.parseInt(name);
                PauseHistoryItem pauseHistoryItem = new PauseHistoryItem(i*1000);
                RecordingManager.getInstance().replaceItem(item, pauseHistoryItem);
                nodeToBeUpdated.setUserObject(new TestStepUserObject(pauseHistoryItem, parentId));
                tree.updateUI();
            } catch (Exception ex) {
                // TODO alert user
            }
        } else if( item instanceof TouchTestCaseItem) {
            showViewPropsFrame(item);
        } else if( item instanceof ShellKeyEventHistoryItem) {

        }
    }

    private void showViewPropsFrame(TestCaseItem item) {

    }

    public void loadTestCase(RecordingManager recordingManager_emulator_events) {
        if(recordingManager_emulator_events == null || recordingManager_emulator_events.getCurrentTestCase() == null || recordingManager_emulator_events.getCurrentTestCase().isEmpty()) {
            L.a(this, "Nothing to edit");
            return;
        }

        TestCase testCase = recordingManager_emulator_events.getCurrentTestCase();

        wipe(testCase.getId());

        DefaultMutableTreeNode testCaseNode = addTestCaseNode(testCase.getId());

        if(testCaseNode.getChildCount() == 0) {
            for (TestCaseItem item : testCase.getTestCaseSteps()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestStepUserObject(item, testCase.getId()));
                testCaseNode.add(node);
            }
            tree.updateUI();
        }
    }

    public DefaultMutableTreeNode addTestCaseNode(long id) {

        for(DefaultMutableTreeNode existedNode: currentTestCaseNodes) {
            TestCaseUserObject testCaseUserObject = (TestCaseUserObject) existedNode.getUserObject();
            if(testCaseUserObject.getTestCaseId() == id) {
                return existedNode;
            }
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TestCaseUserObject("Test case", id));
        testSuiteRootNode.add(node);
        currentTestCaseNodes.add(node);
        return node;
    }

    public JTestRecordingFrame wipe(long id) {
        DefaultMutableTreeNode testCase = getTestCaseNode(id);
        if(testCase == null) return this;

        if(testCase.getChildCount() > 0) {
            testCase.removeAllChildren();
            tree.updateUI();
        }

        return this;
    }

    public void updateUI() {
        tree.updateUI();
    }

    public void startNewReport() {
        mReportFile = new File(String.format("./%d_test_case_report.txt", System.currentTimeMillis()));
    }

    private class TestStepUserObject {
        private TestCaseItem item;
        private Long parentId;

        public TestStepUserObject(TestCaseItem book, Long id) {
            item = book;
            setParentId(id);
        }

        public String toString() {
            return item.toString();
        }

        public TestCaseItem getItem() {
            return item;
        }

        public void setItem(TestCaseItem item) {
            this.item = item;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public Long getParentId() {
            return parentId;
        }
    }

    private class TestCaseUserObject {
        private String testCaseTitle;
        private Long testCaseId;

        public TestCaseUserObject(String testCaseTitle, Long id) {
            setTestCaseTitle(testCaseTitle);
            setTestCaseId(id);
        }

        public void setTestCaseTitle(String testCaseTitle) {
            this.testCaseTitle = testCaseTitle;
        }

        public String getTestCaseTitle() {
            return testCaseTitle;
        }

        public void setTestCaseId(Long testCaseId) {
            this.testCaseId = testCaseId;
        }

        public Long getTestCaseId() {
            return testCaseId;
        }

        @Override
        public String toString() {
            return String.format("Test case, id: %d", getTestCaseId());
        }
    }

    private class TestSuiteUserObject {
        private String testSuiteTitle;

        public TestSuiteUserObject(String title) {
            setTestSuiteTitle(title);
        }

        public void setTestSuiteTitle(String testSuiteTitle) {
            this.testSuiteTitle = testSuiteTitle;
        }

        public String getTestSuiteTitle() {
            return testSuiteTitle;
        }

        @Override
        public String toString() {
            return String.format("%s", getTestSuiteTitle());
        }
    }
}
