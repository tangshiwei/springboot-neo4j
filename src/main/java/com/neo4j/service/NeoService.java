package com.neo4j.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neo4j.model.Classes;
import com.neo4j.model.Lesson;
import com.neo4j.model.Student;
import com.neo4j.model.Teacher;
import com.neo4j.payload.ClassmateInfoGroupByLesson;
import com.neo4j.payload.TeacherStudent;
import com.neo4j.repository.ClassRepository;
import com.neo4j.repository.LessonRepository;
import com.neo4j.repository.StudentRepository;
import com.neo4j.repository.TeacherRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NeoService
 */
@Service
public class NeoService {
    @Autowired
    private ClassRepository classRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 初始化数据
     */
    @Transactional
    public void initData() {
        // 初始化老师
        Teacher zhangsan = Teacher.of("张三老师");
        Teacher lisi = Teacher.of("李四老师");
        Teacher wangwu = Teacher.of("王五老师");
        Teacher liuliu = Teacher.of("刘柳老师");

        teacherRepo.save(zhangsan);
        teacherRepo.save(lisi);
        teacherRepo.save(wangwu);
        teacherRepo.save(liuliu);

        // 初始化课程
        Lesson lessonOne = Lesson.of("数学课", zhangsan);
        Lesson lessonTwo = Lesson.of("物理课", lisi);
        Lesson lessonThree = Lesson.of("化学课", lisi);
        Lesson lessionFour = Lesson.of("外语课", wangwu);
        Lesson lessonFive = Lesson.of("历史课", wangwu);
        Lesson lessonSix = Lesson.of("地理课", liuliu);
        Lesson lessonSeven = Lesson.of("政治课", zhangsan);
        lessonRepo.save(lessonOne);
        lessonRepo.save(lessonTwo);
        lessonRepo.save(lessonThree);
        lessonRepo.save(lessionFour);
        lessonRepo.save(lessonFive);
        lessonRepo.save(lessonSix);
        lessonRepo.save(lessonSeven);

        // 初始化班级
        Classes classOne = Classes.of("小一班", zhangsan);
        Classes classTwo = Classes.of("小二班", lisi);

        Classes classThree = Classes.of("小三班", wangwu);
        Classes classFour = Classes.of("小四班", liuliu);

        classRepo.save(classOne);
        classRepo.save(classTwo);
        classRepo.save(classThree);
        classRepo.save(classFour);

        // 初始化学生
        List<Student> students1 = Lists.newArrayList(Student.of("小明", Lists.newArrayList(lessonOne, lessonThree, lessionFour, lessonFive), classOne), Student
                .of("小强", Lists.newArrayList(lessonTwo, lessonSeven, lessonThree), classTwo), Student.of("小英", Lists.newArrayList(lessonOne, lessonSix, lessonThree), classFour));
        List<Student> students2 = Lists.newArrayList(Student.of("小娟", Lists.newArrayList(lessonTwo), classThree), Student.of("小玲", Lists
                .newArrayList(lessonSeven), classThree), Student.of("小雨", Lists.newArrayList(lessonSix), classThree));


        studentRepo.saveAll(students1);
        studentRepo.saveAll(students2);

    }

    /**
     * 删除数据
     */
    @Transactional
    public void delete() {
        // 使用语句删除
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//        session.query("match (n)-[r]-() delete n,r", Maps.newHashMap());
//        session.query("match (n)-[r]-() delete r", Maps.newHashMap());
//        session.query("match (n) delete n", Maps.newHashMap());
//        transaction.commit();

        // 使用 repository 删除
        studentRepo.deleteAll();
        classRepo.deleteAll();
        lessonRepo.deleteAll();
        teacherRepo.deleteAll();
    }

    /**
     * 根据学生姓名查询所选课程
     *
     * @param studentName 学生姓名
     * @param depth       深度
     * @return 课程列表
     */
    public List<Lesson> findLessonsFromStudent(String studentName, int depth) {
        List<Lesson> lessons = Lists.newArrayList();
        studentRepo.findByName(studentName, depth).ifPresent(student -> lessons.addAll(student.getLessons()));
        return lessons;
    }

    /**
     * 查询全校学生数
     *
     * @return 学生总数
     */
    public Long studentCount(String className) {
        if (StrUtil.isBlank(className)) {
            return studentRepo.count();
        } else {
            return studentRepo.countByClassName(className);
        }
    }

    /**
     * 查询同学关系，根据课程
     *
     * @return 返回同学关系
     */
    public Map<String, List<Student>> findClassmatesGroupByLesson() {
        List<ClassmateInfoGroupByLesson> groupByLesson = studentRepo.findByClassmateGroupByLesson();
        Map<String, List<Student>> result = Maps.newHashMap();

        groupByLesson.forEach(classmateInfoGroupByLesson -> result.put(classmateInfoGroupByLesson.getLessonName(), classmateInfoGroupByLesson
                .getStudents()));

        return result;
    }

    /**
     * 查询所有师生关系，包括班主任/学生，任课老师/学生
     *
     * @return 师生关系
     */
    public Map<String, Set<Student>> findTeacherStudent() {
        List<TeacherStudent> teacherStudentByClass = studentRepo.findTeacherStudentByClass();
        List<TeacherStudent> teacherStudentByLesson = studentRepo.findTeacherStudentByLesson();
        Map<String, Set<Student>> result = Maps.newHashMap();

        teacherStudentByClass.forEach(teacherStudent -> result.put(teacherStudent.getTeacherName(), Sets.newHashSet(teacherStudent
                .getStudents())));

        teacherStudentByLesson.forEach(teacherStudent -> result.put(teacherStudent.getTeacherName(), Sets.newHashSet(teacherStudent
                .getStudents())));

        return result;
    }
}
