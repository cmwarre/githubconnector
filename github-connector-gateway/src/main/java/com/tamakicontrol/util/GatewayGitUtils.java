package com.tamakicontrol.util;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.config.GitSettingsRecord;
import org.eclipse.jgit.api.*;
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

import java.io.File;
import java.io.IOException;
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
        String projectDir = String.format("%s/projects/.git", dataDirectory);
        logger.info(projectDir);
        return new File(projectDir);
    }

    public void init(){
        try {
            repo = FileRepositoryBuilder.create(getProjectDir());
            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while initializing git repo", e);
        }
    }

    public void load(){
        try{
            repo = new FileRepositoryBuilder()
                    .setGitDir(getProjectDir())
                    .readEnvironment()
                    .findGitDir()
                    .setMustExist(true)
                    .build();

            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while loading git repo", e);
        }
    }

    public String getCurrentBranch(){
        try {
            return git.getRepository().getBranch();
        }catch (IOException e){
            logger.error("Exception thrown while getting current branch", e);
            return null;
        }
    }

    private List<Ref> listBranches(){
        try {
            return git.branchList().call();
        }catch (GitAPIException e){
            logger.error("Error thrown while listing branches", e);
            return null;
        }
    }

    private void add(String filePattern){
        try {
            AddCommand add = git.add();
            add.addFilepattern(filePattern);
            add.call();
        }catch (GitAPIException e){
            logger.error("Exception thrown while calling add()", e);
        }
    }

    @Override
    protected void commitImpl(String message, String author, String email) {
        try {
            git.commit().setAuthor(author, email)
                        .setMessage(message)
                        .setAll(true)
                        .call();

        }catch (GitAPIException e){
            logger.error("Error thrown during commit", e);
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
    protected void pullImpl() {
        try {
            PullCommand pullCommand = git.pull();
            pullCommand.call();
        }catch (GitAPIException e ){
            logger.error("Error thrown during push", e);
        }
    }

    @Override
    protected void pushImpl() {
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
        }
    }

    @Override
    protected void checkoutImpl(String branch) {
        try {
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(branch);
            checkoutCommand.setCreateBranch(false);
            checkoutCommand.call();
        }catch (GitAPIException e){
            logger.error("Exception thrown during checkout", e);
        }
    }


}
