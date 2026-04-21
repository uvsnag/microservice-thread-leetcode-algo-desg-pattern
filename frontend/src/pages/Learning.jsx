import { useState } from 'react'
import { learningApi } from '../api/client'

const SECTIONS = [
  {
    id: 'patterns',
    title: 'Design Patterns',
    icon: '🏗️',
    color: '#7c4dff',
    items: [
      { key: 'singleton', label: 'Singleton', desc: 'Double-checked, Bill Pugh, Enum' },
      { key: 'factory', label: 'Factory', desc: 'Notification system (Email/SMS/Push)' },
      { key: 'strategy', label: 'Strategy', desc: 'Pricing calculator (4 strategies)' },
      { key: 'observer', label: 'Observer', desc: 'Stock price monitor' },
      { key: 'builder', label: 'Builder', desc: 'HTTP request builder, fluent API' },
      { key: 'decorator', label: 'Decorator', desc: 'Coffee shop stackable add-ons' },
      { key: 'adapter', label: 'Adapter', desc: 'Payment gateway (Stripe/PayPal)' },
      { key: 'template-method', label: 'Template Method', desc: 'Data export (CSV/JSON/XML)' },
    ],
  },
  {
    id: 'algorithms',
    title: 'Algorithms',
    icon: '⚡',
    color: '#00bfa5',
    items: [
      { key: 'sorting/bubble', label: 'Bubble Sort', desc: 'O(n²), stable, early termination' },
      { key: 'sorting/selection', label: 'Selection Sort', desc: 'O(n²), minimize swaps' },
      { key: 'sorting/insertion', label: 'Insertion Sort', desc: 'O(n) best, nearly sorted' },
      { key: 'sorting/merge', label: 'Merge Sort', desc: 'O(n log n) guaranteed, stable' },
      { key: 'sorting/quick', label: 'Quick Sort', desc: 'O(n log n) avg, Lomuto partition' },
      { key: 'searching/binary', label: 'Binary Search', desc: 'O(log n) vs linear O(n)' },
      { key: 'graph/bfs', label: 'BFS', desc: 'Level-order, shortest path, grid BFS' },
      { key: 'graph/dfs', label: 'DFS', desc: 'Recursive, cycle detection, topological sort' },
    ],
  },
  {
    id: 'data-structures',
    title: 'Data Structures',
    icon: '🧱',
    color: '#ff6d00',
    items: [
      { key: 'linked-list', label: 'Linked List', desc: 'Insert, reverse, cycle detection' },
      { key: 'stack', label: 'Stack', desc: 'LIFO, valid parentheses' },
      { key: 'queue', label: 'Queue', desc: 'Circular queue, priority queue' },
      { key: 'binary-tree', label: 'Binary Tree', desc: 'BST, in/pre/post-order traversal' },
      { key: 'hash-map', label: 'HashMap', desc: 'Custom impl, chaining, rehashing' },
      { key: 'graph', label: 'Graph', desc: 'Adjacency list, BFS, DFS, shortest path' },
    ],
  },
  {
    id: 'multithread',
    title: 'Multithreading',
    icon: '🔄',
    color: '#d50000',
    items: [
      { key: 'thread-basics', label: 'Thread Basics', desc: 'Thread, Runnable, Callable, Future' },
      { key: 'synchronization', label: 'Synchronization', desc: 'synchronized, volatile, wait/notify' },
      { key: 'executor-service', label: 'ExecutorService', desc: 'ThreadPool, CompletableFuture' },
      { key: 'concurrency-utils', label: 'Concurrency Utils', desc: 'Lock, Atomic, Latch, Barrier, Semaphore' },
      { key: 'concurrent-collections', label: 'Concurrent Collections', desc: 'ConcurrentHashMap, BlockingQueue' },
    ],
  },
  {
    id: 'leetcode',
    title: 'LeetCode Practice',
    icon: '💡',
    color: '#ffa000',
    items: [
      { key: 'sliding-window', label: 'Sliding Window (8)', desc: '#3, #76, #424, #567, #438, #713, #643' },
      { key: 'two-pointers', label: 'Two Pointers (6)', desc: '#167, #11, #15, #18, #26, #125' },
      { key: 'prefix-sum', label: 'Prefix Sum (4)', desc: '#560, #523, #303, #238' },
      { key: 'array', label: 'Array (6)', desc: '#56, #57, #189, #73, #54, #134' },
      { key: 'string', label: 'String (6)', desc: '#49, #242, #5, #647, #8, #271' },
      { key: 'bonus', label: 'Bonus (4)', desc: '#347, #75, #283, #169' },
    ],
  },
]

function Learning() {
  const [activeSection, setActiveSection] = useState('patterns')
  const [results, setResults] = useState({})
  const [loading, setLoading] = useState({})
  const [expanded, setExpanded] = useState({})

  const runDemo = async (sectionId, itemKey) => {
    const resultKey = `${sectionId}/${itemKey}`
    setLoading(prev => ({ ...prev, [resultKey]: true }))
    try {
      const { data } = await learningApi.run(sectionId, itemKey)
      const output = data.output || JSON.stringify(data, null, 2)
      setResults(prev => ({ ...prev, [resultKey]: { output, error: null } }))
      setExpanded(prev => ({ ...prev, [resultKey]: true }))
    } catch (err) {
      setResults(prev => ({
        ...prev,
        [resultKey]: { output: null, error: err.response?.data?.message || err.message },
      }))
      setExpanded(prev => ({ ...prev, [resultKey]: true }))
    } finally {
      setLoading(prev => ({ ...prev, [resultKey]: false }))
    }
  }

  const runAll = async (sectionId, items) => {
    for (const item of items) {
      await runDemo(sectionId, item.key)
    }
  }

  const toggleExpand = (key) => {
    setExpanded(prev => ({ ...prev, [key]: !prev[key] }))
  }

  const section = SECTIONS.find(s => s.id === activeSection)

  return (
    <div>
      <h1 className="page-header">Learning Service — CS Fundamentals & LeetCode</h1>

      {/* Section tabs */}
      <div className="learning-tabs">
        {SECTIONS.map(s => (
          <button
            key={s.id}
            className={`learning-tab ${activeSection === s.id ? 'active' : ''}`}
            style={activeSection === s.id ? { borderBottomColor: s.color, color: s.color } : {}}
            onClick={() => setActiveSection(s.id)}
          >
            <span className="tab-icon">{s.icon}</span>
            {s.title}
          </button>
        ))}
      </div>

      {/* Section content */}
      {section && (
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h3 style={{ color: section.color }}>{section.icon} {section.title}</h3>
            <button
              className="btn btn-primary"
              style={{ background: section.color, fontSize: 12 }}
              onClick={() => runAll(section.id, section.items)}
            >
              Run All
            </button>
          </div>

          <div className="learning-grid">
            {section.items.map(item => {
              const resultKey = `${section.id}/${item.key}`
              const result = results[resultKey]
              const isLoading = loading[resultKey]
              const isExpanded = expanded[resultKey]

              return (
                <div key={item.key} className="learning-item">
                  <div className="learning-item-header">
                    <div>
                      <div className="learning-item-title">{item.label}</div>
                      <div className="learning-item-desc">{item.desc}</div>
                    </div>
                    <button
                      className="btn run-btn"
                      style={{ background: section.color }}
                      onClick={() => runDemo(section.id, item.key)}
                      disabled={isLoading}
                    >
                      {isLoading ? '⏳' : '▶'}
                    </button>
                  </div>

                  {result && (
                    <div className="learning-result">
                      <div className="result-toggle" onClick={() => toggleExpand(resultKey)}>
                        {isExpanded ? '▾ Output' : '▸ Output'}
                        {result.error && <span style={{ color: '#ef5350', marginLeft: 8 }}>Error</span>}
                      </div>
                      {isExpanded && (
                        <pre className={`result-content ${result.error ? 'result-error' : ''}`}>
                          {result.error || result.output}
                        </pre>
                      )}
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </div>
      )}
    </div>
  )
}

export default Learning
