package edu.utd.cs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.utd.cs.dao.CodeStatsDAO;
import edu.utd.cs.dao.FileActivityDAO;
import edu.utd.cs.dao.UserActivityDAO;
import edu.utd.cs.model.CodeStatistics;
import edu.utd.cs.model.FileActivityDetails;
import edu.utd.cs.model.GitProjectDetails;
import edu.utd.cs.model.UserActivityDetails;

@Controller
public class StatisticsController {

	@Autowired
	FileActivityDAO fileActivityDAO;

	@Autowired
	UserActivityDAO userActivityDAO;

	@Autowired
	CodeStatsDAO codeStatsDAO;

	@RequestMapping(value = "/statistics**", method = RequestMethod.POST)
	public ModelAndView getStatistics(@ModelAttribute GitProjectDetails detail) {
		List<FileActivityDetails> fileDetails = fileActivityDAO.getFileActivityDetails(detail);
		List<UserActivityDetails> userDetails = userActivityDAO.getUserActivityDetails(detail);
		List<CodeStatistics> codeStats = codeStatsDAO.getCodeStatistics(detail);
		ModelAndView model = new ModelAndView();
		model.addObject("userDetails", userDetails);
		model.addObject("fileDetails", fileDetails);
		model.addObject("codeStats", codeStats);
		model.setViewName("statistics");
		return model;
	}
}
