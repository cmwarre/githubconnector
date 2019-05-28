package com.tamakicontrol.util;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.GitException;
import com.tamakicontrol.config.GitSettingsRecord;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GatewayGitUtils extends AbstractGitUtilProvider {

    private static Logger logger = LoggerFactory.getLogger(GatewayGitUtils.class);

    private GatewayContext context;
    private Repository repo;
    private Git git;

    public GatewayGitUtils(GatewayContext context){
        this.context = context;
    }

    private File getProjectDir(){
        String dataDirectory = context.getSystemManager().getDataDir().getAbsolutePath();
        String projectDir = String.format("%s/projects/", dataDirectory);
        logger.info(projectDir);
        return new File(projectDir);
    }

    public File getGitDir(){
        String projectDir = getProjectDir().getAbsolutePath();
        return new File(String.format("%s/.git", projectDir));
    }

    public void init(){
        logger.debug("Initializing git repo");

        try {
            repo = FileRepositoryBuilder.create(getProjectDir());
            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while initializing git repo", e);
        }
    }

    public void load(){
        logger.debug("loading existing git repo");

        try{
            repo = new FileRepositoryBuilder()
                    .setGitDir(getGitDir())
                    .readEnvironment()
                    .findGitDir()
                    .setMustExist(true)
                    .build();

            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while loading git repo", e);
        }
    }

    public String getCurrentBranch() throws Exception {
        logger.debug("getCurrentBranch called");

        try {
            return git.getRepository().getBranch();
        }catch (IOException e){
            logger.error("Exception thrown while getting current branch", e);
            throw e;
        }
    }

    @Nonnull
    private List<Ref> listBranches() throws Exception {
        try {
            return git.branchList().call();
        }catch (GitAPIException e){
            logger.error("Error thrown while listing branches", e);
            throw e;
        }
    }

    @Override
    protected List<String> getBranchesImpl() throws Exception {
        logger.debug("Listing git branches");
        List<String> branches = new ArrayList<>();

        for(Ref ref : listBranches()){
            branches.add(ref.getName());
        }

        return branches;
    }


    // TODO validate that other branches dont already exist
    @Override
    protected void addBranchImpl(String name) throws Exception {
        logger.debug(String.format("Adding new branch %s", name));
        try{
            Ref result = git.branchCreate().setName(name).call();
            logger.debug(String.format("Created new branch %s", result.toString()));
        }catch (GitAPIException e){
            logger.error("Exception thrown while creating branch", e);
            throw e;
        }
    }


    @Override
    protected void renameBranchImpl(String name, String newName) throws Exception {
        logger.debug(String.format("Renaming branch %s to %s", name, newName));
        try {
            Ref result = git.branchRename().setOldName(name).setNewName(newName).call();
            logger.debug(String.format("Renamed branch %s", result.toString()));
        }catch (GitAPIException e){
            logger.error("Error thrown while renaming branch", e);
            throw e;
        }
    }

    @Override
    public String getCurrentBranchImpl() throws Exception {
        logger.debug("Getting current branch");
        try{
            String branch = git.getRepository().getBranch();
            logger.debug(String.format("Current branch is %s", branch));
            return branch;
        }catch (IOException e){
            logger.error("Error thrown while getting current branch", e);
            throw e;
        }
    }

    @Override
    protected void removeBranchImpl(String name) throws Exception {
        logger.debug(String.format("Removing branch %s", name));

        try{
            List<String> result = git.branchDelete().setBranchNames(name).call();
            logger.debug(String.format("Removed branches %s", StringUtils.join(result, ",")));
        }catch (GitAPIException e){
            logger.error("Exception thrown while deleting branch", e);
            throw e;
        }
    }

    @Override
    protected void addImpl(String filePattern) throws Exception{
        logger.debug(String.format("Adding %s to git", filePattern));
        try {
            AddCommand add = git.add();
            add.addFilepattern(filePattern);
            add.call();
        }catch (GitAPIException e){
            logger.error("Exception thrown while calling add()", e);
            throw e;
        }
    }

    @Override
    protected void commitImpl(String message, String author, String email) throws Exception {
        logger.debug(String.format("Commit by %s: %s", author, message));
        try {
            git.commit().setAuthor(author, email)
                        .setMessage(message)
                        .setAll(true)
                        .call();

        }catch (GitAPIException e){
            logger.error("Error thrown during commit", e);
            throw e;
        }
    }

    private void setRemote(){

        try {
            StoredConfig config = git.getRepository().getConfig();
            // TODO obviously not this either
            config.setString("remote", "origin", "url", "http://github.com/user/repo");
            config.save();
        }catch (IOException e){
            logger.error("Error thrown while setting remote", e);
        }
    }

    @Override
    protected void pullImpl() throws Exception {
        logger.debug("Pulling from remote");

        try {
            PullCommand pullCommand = git.pull();
            pullCommand.call();
        }catch (GitAPIException e ){
            logger.error("Error thrown during push", e);
            throw e;
        }
    }

    @Override
    protected void pushImpl() throws Exception {
        logger.debug("Pushing to remote");

        try {
            GitSettingsRecord settings = context.getPersistenceInterface().find(GitSettingsRecord.META, 0L);
            CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(
                    settings.getUsername(), settings.getPassword());

            Iterable<PushResult> results  = git.push().setRemote("origin")
                    .setCredentialsProvider(credentials)
                    .call();

            logger.info(String.format("Pushed commits to origin.  %s", results.toString()));
        }catch (GitAPIException e ){
            logger.error("Error thrown during push", e);
            throw e;
        }
    }

    @Override
    protected void checkoutImpl(String branch) throws GitException {
        logger.debug("Checking out branch");

        try {
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(branch);
            checkoutCommand.setCreateBranch(false);
            Ref result = checkoutCommand.call();

            logger.debug(String.format("Branch Checkout complete: %s", result.toString()));
        }catch (CheckoutConflictException e){
            throw new GitException(e, e.getConflictingPaths());
        }catch(GitAPIException e){
            logger.error("Exception thrown during checkout", e);
            throw new GitException(e);
        }

    }

}
