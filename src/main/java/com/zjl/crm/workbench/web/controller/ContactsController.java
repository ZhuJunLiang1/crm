package com.zjl.crm.workbench.web.controller;

import com.zjl.crm.settings.domain.User;
import com.zjl.crm.settings.service.UserService;
import com.zjl.crm.utils.DateTimeUtil;
import com.zjl.crm.utils.UUIDUtil;
import com.zjl.crm.vo.PageinationVO;
import com.zjl.crm.workbench.domain.Activity;
import com.zjl.crm.workbench.domain.Contacts;
import com.zjl.crm.workbench.domain.ContactsRemark;
import com.zjl.crm.workbench.domain.Tran;
import com.zjl.crm.workbench.service.ContactsService;
import com.zjl.crm.workbench.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@RequestMapping("workbench/contacts")
@Controller
public class ContactsController {

    @Resource
    private ContactsService contactsService;

    @Resource
    private UserService userService;

    @Resource
    private CustomerService customerService;

    @RequestMapping(value = "/getUserList.do",method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUserList(){
        List<User> userList = userService.getUserList();
        return userList;
    }

    @RequestMapping(value = "/pageList.do",method = RequestMethod.GET)
    @ResponseBody
    public Object pageList(Contacts contacts, Integer pageNo, Integer pageSize){
        Map<String,Object> map = new HashMap<>();
        Integer skipCount = (pageNo-1)*pageSize;
        map.put("contacts",contacts);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        PageinationVO<Contacts> vo = contactsService.pageList(map);
        return vo;
    }

    @RequestMapping(value = "/delete.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> delete(HttpServletRequest request){
        String ids[] = request.getParameterValues("id");
        boolean flag = contactsService.delete(ids);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        return map;
    }

    @RequestMapping(value = "/save.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> save(Contacts contacts,HttpServletRequest request,String customerName){
        contacts.setId(UUIDUtil.getUUID());
        contacts.setCreateTime(DateTimeUtil.getSysTime());
        contacts.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        boolean flag = contactsService.save(contacts,customerName);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        return map;
    }

    @RequestMapping(value = "/getUserListAndContacts.do",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getUserListAndContacts(String id){
        Map<String,Object> map = contactsService.getUserListAndContacts(id);
        return map;
    }

    @RequestMapping(value = "/update.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> update(Contacts contacts,HttpServletRequest request,String customerName){
        contacts.setEditTime(DateTimeUtil.getSysTime());
        contacts.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        Map<String,Object> map = contactsService.update(contacts,customerName);
        return map;
    }

    @RequestMapping(value = "/detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView detail(String id){
        Contacts contacts = contactsService.detail(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("contacts",contacts);
        mv.setViewName("detail.jsp");
        return mv;
    }

    @RequestMapping(value = "/getRemarkListByContactsId.do",method = RequestMethod.GET)
    @ResponseBody
    public List<ContactsRemark> getRemarkListByContactsId(String contactsId){
        return contactsService.getRemarkListByContactsId(contactsId);
    }

    @RequestMapping(value = "/deleteRemarkById.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> deleteRemarkById(String id){
        return contactsService.deleteRemarkById(id);
    }

    @RequestMapping(value = "/saveRemark.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> saveRemark(ContactsRemark contactsRemark,HttpServletRequest request){
        contactsRemark.setId(UUIDUtil.getUUID());
        contactsRemark.setEditFlag("0");
        contactsRemark.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
        return contactsService.saveRemark(contactsRemark);
    }

    @RequestMapping(value = "/updateRemark.do",method = RequestMethod.POST)
    @ResponseBody
    public  Map<String,Object> updateRemark(ContactsRemark contactsRemark,HttpServletRequest request){
        contactsRemark.setEditFlag("1");
        contactsRemark.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        contactsRemark.setEditTime(DateTimeUtil.getSysTime());
        return contactsService.updateRemark(contactsRemark);
    }

    @RequestMapping(value = "/getTranListByContactsId.do",method = RequestMethod.GET)
    @ResponseBody
    public List<Tran> getTranListByContactsId(String contactsId,HttpServletRequest request){
        List<Tran> tranList = contactsService.getTranListByContactsId(contactsId);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        for (Tran tran: tranList){
            String stage = tran.getStage();
            tran.setPossibility(pMap.get(stage));
        }
        return tranList;
    }

    @RequestMapping(value = "/deleteTranById.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> deleteTranById(String id){
        return contactsService.deleteTranById(id);
    }

    @RequestMapping(value = "/getActivityListByContactsId.do",method = RequestMethod.GET)
    @ResponseBody
    public List<Activity> getActivityListByContactsId(String contactsId){
        return contactsService.getActivityListByContactsId(contactsId);
    }

    @RequestMapping(value = "/unbundActivity.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> unbundActivity(String activityId,String contactsId){
        return contactsService.unbundActivity(activityId,contactsId);
    }

    @RequestMapping(value = "/getActivityListByNameAndNotByContactsId.do",method = RequestMethod.GET)
    @ResponseBody
    public List<Activity> getActivityListByNameAndNotByContactsId(String activityName,String contactsId){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("activityName",activityName);
        map.put("contactsId",contactsId);
        return contactsService.getActivityListByNameAndNotByContactsId(map);
    }

    @RequestMapping(value = "/bundActivity.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> bundActivity(String contactsId,HttpServletRequest request){
        String[] aids = request.getParameterValues("activityId");
        boolean flag = contactsService.bund(contactsId,aids);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        return map;
    }

    @RequestMapping(value = "/getCustomerName.do",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getCustomerName(String name){
        return customerService.getCustomerName(name);
    }

    @RequestMapping(value = "/getDetailById.do",method = RequestMethod.GET)
    @ResponseBody
    public Contacts getDetailById(String id){
        return contactsService.detail(id);
    }
}
