package com.dhis2.usescases.main.program;

import android.support.annotation.NonNull;

import com.dhis2.utils.DateUtils;
import com.dhis2.utils.Period;
import com.squareup.sqlbrite2.BriteDatabase;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.program.ProgramModel;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

class HomeRepositoryImpl implements HomeRepository {

    private final static String SELECT_EVENTS = String.format(Locale.US,
            "SELECT * FROM %s WHERE %s.%s = 'programUid' ORDER BY Event.lastUpdated DESC",
            EventModel.TABLE, EventModel.TABLE, EventModel.Columns.PROGRAM);

    private final static String EVENT = "SELECT * FROM Event ORDER BY Event.lastUpdated DESC";

    private final static String PROGRAMS_EVENT_DATES = "" +
            "SELECT *, Program.uid, Event.uid AS event_uid, Event.lastUpdated AS event_updated " +
            "FROM Program " +
            "INNER JOIN Event ON Event.program = Program.uid " +
            "WHERE event_updated BETWEEN '%s' AND '%s' " +
            "GROUP BY Program.uid";

    private final static String PROGRAMS_EVENT_DATES_2 = "" +
            "SELECT *, Program.uid, Event.uid AS event_uid, Event.lastUpdated AS event_updated FROM Program " +
            "INNER JOIN Event ON Event.program = Program.uid "+
            "WHERE (%s) " +
            "GROUP BY Program.uid";

    private final static String SELECT =
            "SELECT *, Program.uid, Event.uid AS event_uid FROM ((Program" +
                    " INNER JOIN Event ON Event.program = Program.uid)" +
                    " INNER JOIN OrganisationUnitProgramLink ON OrganisationUnitProgramLink.program = Program.uid)" +
                    " WHERE (%s) AND OrganisationUnitProgramLink.organisationUnit IN (%s)" +
                    " GROUP BY Program.uid";


    private final static String[] SELECT_TABLE_NAMES = new String[]{ProgramModel.TABLE, EventModel.TABLE, OrganisationUnitProgramLinkModel.TABLE};
    private final static String[] SELECT_TABLE_NAMES_2 = new String[]{ProgramModel.TABLE, EventModel.TABLE};
    private static final Set<String> SELECT_SET = new HashSet<>(Arrays.asList(SELECT_TABLE_NAMES));
    private static final Set<String> SELECT_SET_2 = new HashSet<>(Arrays.asList(SELECT_TABLE_NAMES_2));

    private final static String SELECT_ORG_UNITS =
            "SELECT * FROM " + OrganisationUnitModel.TABLE;

    private final BriteDatabase briteDatabase;

    HomeRepositoryImpl(BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }


    @NonNull
    @Override
    public Observable<List<ProgramModel>> programs(String fromDate, String toDate) {
        return briteDatabase.createQuery(SELECT_SET_2, String.format(PROGRAMS_EVENT_DATES, fromDate, toDate))
                .mapToList(ProgramModel::create);
    }

    @NonNull
    @Override
    public Observable<List<ProgramModel>> programs(List<Date> dates, Period period) {

        StringBuilder dateQuery = new StringBuilder();
        String queryFormat = "(%s BETWEEN '%s' AND '%s') ";
        for (int i = 0; i < dates.size(); i++) {
            Date[] datesToQuery = DateUtils.getInstance().getDateFromDateAndPeriod(dates.get(i), period);
            dateQuery.append(String.format(queryFormat, "event_updated", DateUtils.getInstance().formatDate(datesToQuery[0]), DateUtils.getInstance().formatDate(datesToQuery[1])));
            if (i < dates.size() - 1)
                dateQuery.append("OR ");
        }

        return briteDatabase.createQuery(SELECT_SET_2, String.format(PROGRAMS_EVENT_DATES_2, dateQuery))
                .mapToList(ProgramModel::create);
    }


    @NonNull
    @Override
    public Flowable<List<ProgramModel>> programs(List<Date> dates, Period period, String orgUnitsId) {
        StringBuilder dateQuery = new StringBuilder();
        String queryFormat = "(%s BETWEEN '%s' AND '%s') ";
        for (int i = 0; i < dates.size(); i++) {
            Date[] datesToQuery = DateUtils.getInstance().getDateFromDateAndPeriod(dates.get(i), period);
            dateQuery.append(String.format(queryFormat, "Program.lastUpdated", DateUtils.getInstance().formatDate(datesToQuery[0]), DateUtils.getInstance().formatDate(datesToQuery[1])));
            if (i < dates.size() - 1)
                dateQuery.append("OR ");
        }

        return briteDatabase.createQuery(SELECT_SET, String.format(SELECT, dateQuery, orgUnitsId))
                .mapToList(ProgramModel::create).toFlowable(BackpressureStrategy.LATEST);
    }

    @NonNull
    @Override
    public Observable<List<EventModel>> eventModels(String programUid) {
        String query = SELECT_EVENTS.replace("programUid", programUid);
        return briteDatabase.createQuery(EventModel.TABLE, query)
                .mapToList(EventModel::create);
    }

    @NonNull
    @Override
    public Observable<List<OrganisationUnitModel>> orgUnits() {
        return briteDatabase.createQuery(OrganisationUnitModel.TABLE, SELECT_ORG_UNITS)
                .mapToList(OrganisationUnitModel::create);
    }
    @NonNull
    @Override
    public Observable<List<EventModel>> eventModels() {
        return briteDatabase.createQuery(EventModel.TABLE, EVENT)
                .mapToList(EventModel::create);
    }
}
