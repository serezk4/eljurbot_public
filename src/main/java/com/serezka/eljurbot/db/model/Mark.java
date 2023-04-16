package com.serezka.eljurbot.db.model;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.api.school.EljurApi;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Table(name = "marks")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter @ToString
@NoArgsConstructor
@Log4j2
public class Mark {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long markId;

    Long userId;

    String subject;

    String mark;

    String comment;

    String lesson_comment;

    Calendar date;

    public Mark(long userId, String subject, String mark, String comment, String lessonComment, Calendar date) {
        this.userId = userId;
        this.subject = subject;
        this.mark = mark;
        this.comment = comment;
        this.lesson_comment = lessonComment;
        this.date = date;
    }

    public static Mark parse(EljurApi.Marks.Mark apiMark, String subject) {
        Calendar cal = GregorianCalendar.getInstance();
        try {
            cal.setTime(Application.MARKS_DATE_FORMAT.parse(apiMark.getDate()));
        } catch (ParseException e) {
            log.warn(e.getMessage());
            return null;
        }

        return new Mark(-1, subject, apiMark.getValue(), apiMark.getComment(), apiMark.getLesson_comment(), cal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark toCompare = (Mark) o;

        return /*userId.equals(toCompare.userId) &&*/
                subject.equalsIgnoreCase(toCompare.subject) &&
                mark.equalsIgnoreCase(toCompare.mark) && date.compareTo(toCompare.getDate()) == 0;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
