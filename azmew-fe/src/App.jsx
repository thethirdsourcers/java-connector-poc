import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import axios from 'axios';
import { Share2, Facebook, MessageCircle, RefreshCw, CheckCircle, Send, Sparkles } from 'lucide-react';
import './App.css';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [pages, setPages] = useState([]);
  const [selectedPage, setSelectedPage] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [seeding, setSeeding] = useState(false);

  const fetchPages = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE}/pages`);
      setPages(response.data);
      if (response.data.length > 0 && !selectedPage) {
        setSelectedPage(response.data[0]);
      }
    } catch (err) {
      console.error('Failed to fetch pages:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchMessages = async (pageId) => {
    try {
      const response = await axios.get(`${API_BASE}/messages/${pageId}`);
      setMessages(response.data);
    } catch (err) {
      console.error('Failed to fetch messages:', err);
    }
  };

  const seedData = async () => {
    try {
      setSeeding(true);
      await axios.post(`${API_BASE}/pages/seed`);
      await fetchPages();
    } catch (err) {
      console.error('Seeding failed:', err);
    } finally {
      setSeeding(false);
    }
  };

  useEffect(() => {
    fetchPages();
  }, []);

  useEffect(() => {
    if (selectedPage) {
      fetchMessages(selectedPage.id);
      const interval = setInterval(() => fetchMessages(selectedPage.id), 5000);
      return () => clearInterval(interval);
    }
  }, [selectedPage]);

  return (
    <div className="dashboard-container">
      <motion.header
        className="glass-header"
        initial={{ y: -20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <div className="logo-section">
          <motion.div
            animate={{ rotate: [0, 360] }}
            transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
          >
            <Share2 size={32} className="accent-icon" />
          </motion.div>
          <h1>Azmew <span className="sub-logo">Connector</span></h1>
        </div>
        <div className="status-badge">
          <motion.div
            animate={{ scale: [1, 1.2, 1] }}
            transition={{ duration: 2, repeat: Infinity }}
          >
            <CheckCircle size={16} />
          </motion.div>
          <span>Java Backend Active</span>
        </div>
      </motion.header>

      <main className="main-content grid-layout">
        <motion.aside
          className="accounts-sidebar"
          initial={{ x: -20, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <div className="section-header">
            <h3><Sparkles size={16} className="inline-icon" /> Accounts</h3>
            <motion.button
              className="btn-icon-small"
              onClick={seedData}
              title="Seed Data"
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
            >
              <RefreshCw size={14} className={seeding ? 'spin' : ''} />
            </motion.button>
          </div>

          <div className="sidebar-actions">
            <motion.button
              className="btn-mini fb"
              onClick={() => window.location.href = `${API_BASE}/auth/facebook`}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <Facebook size={14} /> FB
            </motion.button>
            <motion.button
              className="btn-mini tiktok"
              onClick={() => window.location.href = `${API_BASE}/auth/tiktok`}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <Share2 size={14} /> TT
            </motion.button>
          </div>

          <div className="account-list">
            <AnimatePresence>
              {pages.map((page, index) => (
                <motion.div
                  key={page.id}
                  className={`account-item ${selectedPage?.id === page.id ? 'active' : ''}`}
                  onClick={() => setSelectedPage(page)}
                  initial={{ x: -20, opacity: 0 }}
                  animate={{ x: 0, opacity: 1 }}
                  exit={{ x: -20, opacity: 0 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ x: 5 }}
                >
                  <div className="platform-dot" data-platform={page.platform}></div>
                  <div className="account-info">
                    <span className="name">{page.pageName}</span>
                    <span className="platform">{page.platform.toLowerCase()}</span>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        </motion.aside>

        <motion.section
          className="inbox-section"
          initial={{ x: 20, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.3 }}
        >
          <AnimatePresence mode="wait">
            {selectedPage ? (
              <motion.div
                key={selectedPage.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.3 }}
              >
                <div className="inbox-header">
                  <div className="inbox-title-area">
                    <h2>{selectedPage.pageName} <span className="badge">Inbox</span></h2>
                    <div className="meta-info">Page ID: {selectedPage.pageId}</div>
                  </div>
                  <div className="platform-indicator" data-platform={selectedPage.platform}>
                    {selectedPage.platform}
                  </div>
                </div>

                <div className="message-list">
                  <AnimatePresence>
                    {messages.length === 0 ? (
                      <motion.div
                        className="empty-chat"
                        initial={{ scale: 0.8, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        exit={{ scale: 0.8, opacity: 0 }}
                      >
                        <MessageCircle size={48} />
                        <p>No messages yet for this account.</p>
                        <span className="hint">Messages will appear here in real-time</span>
                      </motion.div>
                    ) : (
                      messages.map((msg, index) => (
                        <motion.div
                          key={msg.id}
                          className={`message-bubble ${msg.isFromUser ? 'sent' : 'received'}`}
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0 }}
                          transition={{ delay: index * 0.05 }}
                        >
                          <div className="msg-header">
                            <span className="sender">{msg.senderName}</span>
                            <span className="time">{new Date(msg.timestamp).toLocaleTimeString()}</span>
                          </div>
                          <p className="msg-content">{msg.content}</p>
                        </motion.div>
                      ))
                    )}
                  </AnimatePresence>
                </div>
              </motion.div>
            ) : (
              <motion.div
                className="empty-state-full"
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
              >
                <Share2 size={64} className="accent-icon" />
                <h2>Welcome to Azmew Java PoC</h2>
                <p>Connect a platform or use Seed Data to begin tracking messages.</p>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.section>
      </main>
    </div>
  );
}

export default App;
