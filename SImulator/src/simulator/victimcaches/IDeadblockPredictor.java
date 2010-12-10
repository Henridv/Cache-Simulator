package simulator.victimcaches;

/**
 *
 * @author henri
 */
public interface IDeadblockPredictor {

	/**
	 * Needs to be called every time a cacheBlock is referenced
	 * @param cacheBlock
	 * @return TRUE if cacheBlock is dead after this reference
	 */
	public boolean access(long cacheBlock, long programCounter);

	/**
	 * Needs to be called when a cacheBlock is evicted from the cache
	 * @param cacheBlock - cacheBlock being evicted
	 */
	public void evict(long cacheBlock);

	/**
	 *
	 * @param cacheBlock
	 * @return TRUE if cacheBlock is dead
	 */
	public boolean isDead(long cacheBlock);
}
