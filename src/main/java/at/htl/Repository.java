package at.htl;

import at.htl.model.Penalty;
import at.htl.model.Player;
import at.htl.results.GenderCount;
import at.htl.results.MinMaxAmount;
import at.htl.results.PlayerPenalties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class Repository {

    @Inject
    private EntityManager em;

    public List<Player> getAllPlayers() {
        return em.createQuery("Select p from Player p", Player.class).getResultList();
    }

    /**
     * Returns players living in a specified town
     *
     * @param town name of the town
     */
    public List<Player> getPlayersLivingInTown(String town) {
        TypedQuery<Player> query = em.createQuery("Select p from Player p where p.town = :town", Player.class);
        query.setParameter("town", town);
        return query.getResultList();
    }

    /**
     * Returns players of a certain gender born before a specified year
     *
     * @param female         male or female
     * @param bornBeforeYear the exclusive year before someone has to be born
     */
    public List<Player> getPlayersByGenderAndAge(boolean female, int bornBeforeYear) {
        TypedQuery<Player> query = em.createQuery("Select p from Player p where p.yearOfBirth < :bornBeforeYear and p.sex = :sex", Player.class);
        query.setParameter("bornBeforeYear", bornBeforeYear);
        query.setParameter("sex", female ? 'F' : 'M');
        return query.getResultList();
    }

    /**
     * Returns penalties issued between two dates
     *
     * @param start the first (earlier) date, inclusive
     * @param end   the second (later) date, inclusive
     */
    public List<Penalty> getPenaltiesInDateRange(LocalDate start, LocalDate end) {
        TypedQuery<Penalty> q = em.createQuery("SELECT p from Penalty p where p.penDate < :end and p.penDate > :start", Penalty.class);
        q.setParameter("end", end);
        q.setParameter("start", start);
        return q.getResultList();
    }

    /**
     * Returns penalties with an amount higher or equal to the specified amount
     */
    public List<Penalty> getPenaltiesWithAmountHigherEqualThan(BigDecimal amount) {
        return Collections.emptyList();
    }

    /**
     * add Penalty to Player
     *
     * @param playerNo
     * @param penDate
     * @param amount
     */

    @Transactional
    public void addNewPenaltyForPlayer(Long playerNo, LocalDate penDate, BigDecimal amount) {
        Player p = em.find(Player.class,playerNo);

        TypedQuery<Penalty> q2 = em.createQuery("select p from Penalty p order by p.paymentNo desc",Penalty.class);
        long paymentNoN = q2.getResultList().get(0).getPaymentNo() + 1 ;

        Penalty pen = new Penalty();

        pen.setPaymentNo(paymentNoN);
        pen.setPenDate(penDate);
        pen.setAmount(amount);
        pen.setPlayer(p);

        p.getPenalties().add(pen);

        em.persist(pen);

    }

    @Transactional
    public void changePenaltyAmount(long paymentNo, BigDecimal amount){
        Penalty p = em.find(Penalty.class,paymentNo);
        p.setAmount(amount);
    }

    @Transactional
    public Penalty deletePenalty(long paymentNo){
        Penalty pen = em.find(Penalty.class,paymentNo);

        pen.getPlayer().getPenalties().remove(pen);

        em.remove(pen);


        return pen;
    }

    /**
     * Returns the average penalty sum calculated over all penalties
     */
    public Double getAveragePenaltyAmount() {
        return null;
    }

    /**
     * Returns the min & max penalty amount
     */
    public MinMaxAmount getMinMaxPenaltyAmount() {
        return null;
    }

    /**
     * Returns all player numbers who have received a penalty so far
     */
    public List<Long> getPlayerNosWithPenalty() {

        return Collections.emptyList();
    }

    /**
     * Returns all players who have received a penalty so far
     */
    public List<Player> getPlayersWithPenalty() {
        return Collections.emptyList();
    }

    /**
     * Returns all players who either have or have not received a penalty so far
     *
     * @param hasPenalty flag indicating if we want to look for players with or without penalties
     */
    public List<Player> getPlayersWithPenalty(boolean hasPenalty) {
        return Collections.emptyList();
    }

    /**
     * Returns the names of those towns who have at least as many players as specified
     *
     * @param minNoOfPlayers the min. number of players a town has to have
     */
    public List<String> getTownsWithPlayerNumber(Long minNoOfPlayers) {
        return Collections.emptyList();
    }

    /**
     * Returns the number of players for each gender
     */
    public List<GenderCount> getPlayerCountsByGender() {
        return Collections.emptyList();
    }

    /**
     * Returns the penalty sum for all players, including those who never received a penalty (sum = 0)
     */
    public List<PlayerPenalties> getPenaltiesForAllPlayers() {
//        @SuppressWarnings("JpaQlInspection") // IntelliJ thinks coalesce returns an int while it's actually a BigDecimal
        return em.createQuery("SELECT NEW at.htl.results.PlayerPenalties(p, SUM(pen.amount)) from Player p left join p.penalties pen group by p",PlayerPenalties.class).getResultList();
    }


}
