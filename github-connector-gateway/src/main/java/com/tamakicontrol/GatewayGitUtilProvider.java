package com.tamakicontrol;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GatewayGitUtilProvider extends AbstractGitUtilProvider {

    private static Logger logger = LoggerFactory.getLogger(GatewayGitUtilProvider.class);

    private GatewayContext context;
    private Repository repo;
    private Git git;

    public GatewayGitUtilProvider(GatewayContext context){
        this.context = context;
    }

    private File getProjectDir(){
        String dataDirectory = context.getSystemManager().getDataDir().getAbsolutePath();
        return new File(String.format("%s/projects", dataDirectory));
    }

    private void init(){
        try {
            repo = FileRepositoryBuilder.create(getProjectDir());
            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while initializing git repo", e);
        }
    }

    private void load(){
        try{
            repo = new FileRepositoryBuilder().setGitDir(getProjectDir()).build();
            git = new Git(repo);
        }catch (IOException e){
            logger.error("Exception thrown while loading git repo", e);
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
    protected void commitImpl() {
        try {
            CommitCommand commitCommand = git.commit();
            commitCommand.setAuthor("cwarren", "cody@tamaki.co.nz");
            commitCommand.setMessage("");
            commitCommand.call();
        }catch (GitAPIException e){
            logger.error("Error thrown during commit", e);
        }

    }

    private void setRemote(){
        try {
            StoredConfig config = git.getRepository().getConfig();
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
            PushCommand pushCommand = git.push();
            pushCommand.call();
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
