package edu.utd.cs.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.utd.cs.common.CommonInfo;
import edu.utd.cs.dao.ProjectDetailsDAO;
import edu.utd.cs.git.stats.GitJobs;
import edu.utd.cs.model.BranchDetails;
import edu.utd.cs.model.GitProjectDetails;
import edu.utd.cs.service.jenkins.JenkinsJobConfigurer;

@Controller
public class ProfileController {

	@Autowired
	ProjectDetailsDAO projectDetailsDAO;

	@Autowired
	JenkinsJobConfigurer jenkinsJobConfigurer;

	@Autowired
	GitJobs gitJobs;

	@RequestMapping(value = "/profile**", method = RequestMethod.GET)
	public ModelAndView profile() {
		return getProfileDetails(null);
	}

	@RequestMapping(value = "/profile**", method = RequestMethod.POST)
	public ModelAndView profileUpdate(@ModelAttribute GitProjectDetails projectDetails) {

		String projectURL = projectDetails.getProjectUrl();
		int index = projectURL.indexOf(".com/");
		int lastIndex = projectURL.lastIndexOf(".");
		String projectName = projectURL.substring(index + 5, lastIndex);
		String branchName = projectDetails.getBranchDetails().get(0).getBranchName();
		List<String> appUsers = new ArrayList<String>();
		appUsers.add(projectDetails.getBranchDetails().get(0).getLastAppUser());
		projectDetails.getBranchDetails().get(0).setApplUsers(appUsers);
		projectDetails.setProjectName(projectName);
		String rootPOMLoc = projectDetails.getRootPOMLoc();
		jenkinsJobConfigurer.setupJob(projectName, branchName, rootPOMLoc);
		ModelAndView model = getProfileDetails(projectDetails);
		gitJobs.gitResult(projectName, branchName);
		return model;
	}

	@RequestMapping(value = "/updateprofile**", method = RequestMethod.POST)
	public String UpdateAllStatistics() {
		String userName = CommonInfo.getUserName();
		List<GitProjectDetails> projectDetailsList = projectDetailsDAO.getProjectDetails(userName);
		for (GitProjectDetails gitProjectDetail : projectDetailsList) {
			for (BranchDetails branchDetail : gitProjectDetail.getBranchDetails()) {
				if (branchDetail.getApplUsers().contains(userName)) {
					gitJobs.gitResult(gitProjectDetail.getProjectName(), branchDetail.getBranchName());
				}
			}
		}
		return "redirect:/profile";
	}

	public ModelAndView getProfileDetails(GitProjectDetails projectDetails) {
		String userName = CommonInfo.getUserName();
		if (projectDetails != null) {
			projectDetailsDAO.insertProjectDetails(userName, projectDetails);
			projectDetails = null;
		}
		ModelAndView model = new ModelAndView();
		List<GitProjectDetails> projectDetailsList = projectDetailsDAO.getProjectDetails(CommonInfo.getUserName());
		model.addObject("projectDetails", projectDetailsList);
		model.setViewName("profile");
		return model;
	}

}
